"use client";

import { useEffect, useRef } from "react";
import * as BABYLON from "@babylonjs/core";
import * as GUI from "@babylonjs/gui";
import "@babylonjs/loaders";
import { useSurgeryStore } from "../store/surgeryStore";
import Link from "next/link";
import { Button } from "@/app/components/ui/button";
import { mostrarInstrucciones } from "./Instrucciones";
import { API_URL } from "../lib/config";

export default function BabylonScene() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const setEvent = useSurgeryStore((s) => s.setEvent);

  function reiniciarSimulacion() {
    if (websocketRef.current) {
      websocketRef.current.close();
    }

    window.location.reload();
  }

  // Variables para conexión con websocket
  const websocketRef = useRef<WebSocket | null>(null);
  let currentSurgeryId: string | null = null;

  const WS_URL = process.env.NEXT_PUBLIC_WS_URL;

  function getTokenFromCookies() {
    console.log("🔍 Iniciando búsqueda del token en cookies...");
    console.log("🍪 document.cookie:", document.cookie);

    const cookies = document.cookie.split("; ");
    console.log("🍪 Cookies parseadas:", cookies);
    console.log("📊 Total de cookies:", cookies.length);

    for (const cookie of cookies) {
      const [name, value] = cookie.split("=");
      console.log(`  ➡️ Cookie - Nombre: "${name}", Valor: "${value}"`);

      if (name === "jwt-token") {
        console.log("✅ ¡Token encontrado!");
        console.log("🔐 Token JWT:", value);
        return value;
      }
    }

    console.error("❌ Token JWT no encontrado en las cookies");
    console.warn("⚠️ Cookies disponibles:", document.cookie || "Sin cookies");
    return null;
  }

  function getSurgeryIdFromCookies() {
    console.log("🔍 Buscando surgeryId en cookies...");

    const cookies = document.cookie.split("; ");
    console.log("🍪 Cookies disponibles:", cookies);

    for (const cookie of cookies) {
      const [name, value] = cookie.split("=");
      console.log(`  ➡️ Cookie - Nombre: "${name}", Valor: "${value}"`);

      if (name === "lastSurgeryId") {
        console.log("✅ ¡SurgeryId encontrado!");
        console.log("🆔 SurgeryId:", value);
        return value;
      }
    }

    console.warn("⚠️ SurgeryId no encontrado en las cookies");
    return null;
  }

  function conectarWebSocket() {
    console.log("🔌 Iniciando conexión WebSocket...");
    const token = getTokenFromCookies();

    if (!token) {
      console.error(
        "❌ No hay token disponible - No se puede conectar al WebSocket",
      );
      return;
    }

    console.log("✅ Token obtenido correctamente");
    console.log(`📍 URL WebSocket: ${WS_URL}/ws/simulation?token=${token}`);

    websocketRef.current = new WebSocket(
      `${WS_URL}/ws/simulation?token=${token}`,
    );

    websocketRef.current.onopen = () => {
      console.log("✅ WebSocket conectado exitosamente");
      console.log("🟢 Estado WebSocket:", websocketRef.current?.readyState);

      console.log("📤 Enviando evento START al servidor...");
      enviarEvento(0, 0, 0, "START");
    };

    websocketRef.current.onmessage = (event) => {
      const message = JSON.parse(event.data);
      console.log("📨 Mensaje del servidor recibido:", message);

      if (message.status === "SAVED") {
        const surgeryId: string = message.surgeryId;
        currentSurgeryId = surgeryId;
        console.log("💾 ¡Cirugía guardada!");
        console.log("📋 ID de la cirugía:", surgeryId);

        // Guardar en cookies con nombre y valor
        document.cookie = `lastSurgeryId=${surgeryId}; path=/; max-age=86400`; // 24 horas
        console.log(
          "🍪 Cookie de cirugía guardada:",
          `lastSurgeryId=${surgeryId}`,
        );

        // Consultar trayectoria AHORA que sí tenemos el ID
        consultarTrayectoria().then((data) => {
          // Iniciar polling para esperar el análisis de la IA
          iniciarPollingAnalisis();
          // Cerrar conexión WebSocket después de terminar todo
          websocketRef.current?.close();
        });
      }
    };

    websocketRef.current.onerror = (error) => {
      console.error("❌ Error en WebSocket:", error);
      console.error("📊 Tipo de error:", error.type);
    };

    websocketRef.current.onclose = () => {
      console.log("🔌 WebSocket desconectado");
      console.log("🟠 Estado WebSocket:", websocketRef.current?.readyState);
    };
  }

  function enviarEvento(x: any, y: any, z: any, evento = "NONE") {
    if (
      !websocketRef.current ||
      websocketRef.current.readyState !== WebSocket.OPEN
    ) {
      console.warn(
        "⚠️ WebSocket no está abierto. Estado:",
        websocketRef.current?.readyState,
        "- Evento no enviado:",
        evento,
      );
      return;
    }

    const telemetry = {
      coordinates: [x, y, z],
      event: evento,
      timestamp: Date.now(),
    };

    console.log("📤 Enviando evento:", evento, "- Telemetría:", telemetry);
    websocketRef.current.send(JSON.stringify(telemetry));
  }

  async function consultarTrayectoria() {
    console.log("🔍 Consultando trayectoria...");

    // Obtener surgeryId de las cookies
    const surgeryId = getSurgeryIdFromCookies();

    if (!surgeryId) {
      console.error(
        "❌ No se puede consultar trayectoria: surgeryId no encontrado en cookies",
      );
      return null;
    }

    console.log("✅ SurgeryId obtenido de cookies:", surgeryId);

    const token = getTokenFromCookies();

    if (!token) {
      console.error(
        "❌ No se puede consultar trayectoria: token no encontrado",
      );
      return null;
    }

    console.log("✅ Token obtenido para consultar trayectoria");

    try {
      const response = await fetch(
        `${API_URL}/api/v1/surgeries/${surgeryId}/trajectory`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );

      if (response.ok) {
        const data = await response.json();
        console.log("📊 Datos de cirugía obtenidos:", data);
        return data;
      } else {
        console.error("❌ Error al obtener trayectoria:", response.status);
        return null;
      }
    } catch (error) {
      console.error("❌ Error de red al consultar trayectoria:", error);
      return null;
    }
  }

  let pollingInterval: NodeJS.Timeout | null = null;

  function iniciarPollingAnalisis() {
    console.log("⏳ Iniciando polling para análisis de IA...");

    // Limpiar intervalo previo si existe
    if (pollingInterval) clearInterval(pollingInterval);

    pollingInterval = setInterval(async () => {
      const data = await consultarTrayectoria();

      if (data && data.score !== null && data.feedback !== null) {
        console.log("🎯 ¡Análisis de IA recibido!");
        mostrarAnalisisUI(data.score, data.feedback);
        if (pollingInterval) clearInterval(pollingInterval);
      } else {
        console.log("Waiting for AI analysis...");
      }
    }, 5000); // Cada 5 segundos
  }

  let analysisPanel: GUI.Rectangle | null = null;
  let scoreText: GUI.TextBlock | null = null;
  let feedbackText: GUI.TextBlock | null = null;

  function mostrarAnalisisUI(score: number, feedback: string) {
    if (analysisPanel && scoreText && feedbackText) {
      scoreText.text = `PUNTUACIÓN IA: ${score.toFixed(1)}/100`;
      feedbackText.text = `FEEDBACK: ${feedback}`;
      analysisPanel.isVisible = true;
    }
  }

  useEffect(() => {
    if (!canvasRef.current) return;

    const engine = new BABYLON.Engine(canvasRef.current, true);
    const scene = new BABYLON.Scene(engine);
    const backgroundLayer = new BABYLON.Layer(
      "backgroundLayer",
      "/assets/Fondo_Cirujia.png",
      scene,
      true,
    );
    backgroundLayer.isBackground = true;
    if (backgroundLayer.texture) {
      const layerTexture = backgroundLayer.texture as BABYLON.Texture;
      layerTexture.uScale = 1;
      layerTexture.vScale = 1;
      layerTexture.wrapU = BABYLON.Texture.CLAMP_ADDRESSMODE;
      layerTexture.wrapV = BABYLON.Texture.CLAMP_ADDRESSMODE;
    }

    // ajustar tamaño del fondo (escala de la textura)
    // uScale y vScale controlan la repetición/escala horizontal/vertical
    if (backgroundLayer.texture) {
      const layerTexture = backgroundLayer.texture as BABYLON.Texture;
      layerTexture.uScale = 0.7;
      layerTexture.vScale = 0.9;
    }

    // Interfaz de usuario
    const advancedTexture = GUI.AdvancedDynamicTexture.CreateFullscreenUI("UI");

    // Panel para mostrar coordenadas y botones
    const infoPanel = new GUI.Rectangle();
    infoPanel.width = "280px";
    infoPanel.height = "240px";
    infoPanel.cornerRadius = 20;
    infoPanel.color = "black";
    infoPanel.thickness = 2;
    infoPanel.background = "rgba(255, 240, 240, 0.9)";
    infoPanel.horizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_RIGHT;
    infoPanel.verticalAlignment = GUI.Control.VERTICAL_ALIGNMENT_TOP;
    infoPanel.paddingRight = "20px";
    infoPanel.top = "200px";

    advancedTexture.addControl(infoPanel);

    // StackPanel para organizar el texto
    const stack = new GUI.StackPanel();
    stack.paddingTop = "15px";
    stack.paddingLeft = "15px";
    stack.paddingRight = "15px";

    infoPanel.addControl(stack);

    // Título del panel
    const title = new GUI.TextBlock();
    title.text = "Coordenadas del Instrumento";
    title.color = "black";
    title.fontSize = 16;
    title.height = "30px";
    title.paddingBottom = "10px";
    title.fontStyle = "bold";
    title.textHorizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_LEFT;

    stack.addControl(title);

    // TextBlocks para coordenadas X, Y, Z
    const textX = new GUI.TextBlock();
    textX.color = "black";
    textX.fontSize = 18;
    textX.height = "30px";
    textX.textHorizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_LEFT;

    const textY = new GUI.TextBlock();
    textY.color = "black";
    textY.fontSize = 18;
    textY.height = "30px";
    textY.textHorizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_LEFT;

    const textZ = new GUI.TextBlock();
    textZ.color = "black";
    textZ.fontSize = 18;
    textZ.height = "30px";
    textZ.textHorizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_LEFT;

    stack.addControl(textX);
    stack.addControl(textY);
    stack.addControl(textZ);

    // PANEL DE ANÁLISIS IA (Inicia oculto)
    analysisPanel = new GUI.Rectangle();
    analysisPanel.width = "350px";
    analysisPanel.height = "520px";
    analysisPanel.cornerRadius = 20;
    analysisPanel.color = "#00d4ff";
    analysisPanel.background = "rgba(10,20,30,0.9)";
    analysisPanel.thickness = 2;
    analysisPanel.isVisible = false;

    analysisPanel.horizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_LEFT;
    analysisPanel.verticalAlignment = GUI.Control.VERTICAL_ALIGNMENT_CENTER;
    analysisPanel.left = "20px";

    advancedTexture.addControl(analysisPanel);

    const analysisStack = new GUI.StackPanel();
    analysisStack.paddingTop = "15px";
    analysisStack.paddingLeft = "15px";
    analysisStack.paddingRight = "15px";
    analysisStack.spacing = 10;

    analysisPanel.addControl(analysisStack);

    const analysisTitle = new GUI.TextBlock();
    analysisTitle.text = "ANÁLISIS DE IA";
    analysisTitle.color = "#00eaff";
    analysisTitle.fontSize = 22;
    analysisTitle.height = "40px";
    analysisTitle.fontWeight = "bold";

    analysisStack.addControl(analysisTitle);

    scoreText = new GUI.TextBlock();
    scoreText.text = "PUNTUACIÓN IA: --/100";
    scoreText.color = "#7CFFB2";
    scoreText.fontSize = 20;
    scoreText.height = "40px";

    analysisStack.addControl(scoreText);

    const scrollViewer = new GUI.ScrollViewer();
    scrollViewer.height = "340px";
    scrollViewer.thickness = 0;
    scrollViewer.barSize = 10;
    scrollViewer.barColor = "#00d4ff";
    scrollViewer.background = "rgba(255,255,255,0.03)";
    scrollViewer.forceVerticalBar = true;

    analysisStack.addControl(scrollViewer);

    const feedbackContainer = new GUI.StackPanel();
    feedbackContainer.width = "100%";
    feedbackContainer.isVertical = true;

    scrollViewer.addControl(feedbackContainer);

    feedbackText = new GUI.TextBlock();
    feedbackText.text = "Esperando análisis...";
    feedbackText.color = "#ffffff";
    feedbackText.fontSize = 14;
    feedbackText.textWrapping = true;

    feedbackText.width = "100%";
    feedbackText.resizeToFit = true;
    feedbackText.textHorizontalAlignment =
      GUI.Control.HORIZONTAL_ALIGNMENT_LEFT;

    feedbackContainer.addControl(feedbackText);

    const closeAnalysisBtn = GUI.Button.CreateSimpleButton(
      "closeAnalysis",
      "CERRAR",
    );

    closeAnalysisBtn.width = "120px";
    closeAnalysisBtn.height = "40px";
    closeAnalysisBtn.color = "white";
    closeAnalysisBtn.cornerRadius = 10;
    closeAnalysisBtn.background = "#00a8cc";

    closeAnalysisBtn.onPointerUpObservable.add(() => {
      if (analysisPanel) {
        analysisPanel.isVisible = false;
      }
    });

    analysisStack.addControl(closeAnalysisBtn);

    const spacer = new GUI.Rectangle();
    spacer.height = "20px";
    spacer.thickness = 0;
    stack.addControl(spacer);

    // Botón para iniciar simulación
    const startButton = GUI.Button.CreateSimpleButton(
      "startBtn",
      "INICIAR CIRUGÍA",
    );

    startButton.width = "200px";
    startButton.height = "80px";
    startButton.color = "white";
    startButton.cornerRadius = 10;
    startButton.background = "#008000";
    startButton.fontSize = 16;

    startButton.horizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_CENTER;
    startButton.verticalAlignment = GUI.Control.VERTICAL_ALIGNMENT_BOTTOM;
    startButton.paddingBottom = "40px";
    startButton.paddingRight = "20px";

    // Botón para terminar simulación
    const endButton = GUI.Button.CreateSimpleButton(
      "endBtn",
      "TERMINAR CIRUGÍA",
    );

    endButton.width = "200px";
    endButton.height = "80px";
    endButton.color = "white";
    endButton.cornerRadius = 10;
    endButton.background = "#8B0000";
    endButton.fontSize = 16;

    endButton.horizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_CENTER;
    endButton.verticalAlignment = GUI.Control.VERTICAL_ALIGNMENT_BOTTOM;
    endButton.paddingBottom = "40px";
    endButton.paddingRight = "20px";

    stack.addControl(startButton);
    stack.addControl(endButton);
    endButton.isVisible = false;

    mostrarInstrucciones();

    // Cámara laparoscópica
    const camera = new BABYLON.ArcRotateCamera(
      "camera",
      -Math.PI / 2,
      Math.PI / 2.5,
      10,
      BABYLON.Vector3.Zero(),
      scene,
    );
    camera.attachControl(canvasRef.current, true);

    camera.wheelPrecision = 100;
    camera.lowerRadiusLimit = 6;
    camera.upperRadiusLimit = 20;
    camera.inertia = 0.8;

    // Luz quirúrgica
    new BABYLON.HemisphericLight("light", new BABYLON.Vector3(0, 1, 0), scene);

    let scalpelMesh: BABYLON.AbstractMesh | null = null;
    const tumorFragments: BABYLON.Mesh[] = [];
    let arteryMesh: BABYLON.AbstractMesh | null = null;
    let kidneyBodyMesh: BABYLON.AbstractMesh | null = null;

    let sceneReady = false;
    let instrumentActive = false;
    let arteryCut = false;
    let kidneyTouched = false;
    let simulationStarted = false;
    let simulationEnded = false;

    const cutter = BABYLON.MeshBuilder.CreateBox(
      "cutter",
      { width: 0.2, height: 0.2, depth: 1 },
      scene,
    );

    cutter.isVisible = false;

    // Cargar modelos
    Promise.all([
      BABYLON.SceneLoader.ImportMeshAsync("", "/models/", "kidney.glb", scene),
      BABYLON.SceneLoader.ImportMeshAsync("", "/models/", "scalpel.glb", scene),
    ]).then(([kidneyResult, scalpelResult]) => {
      // Configurar riñón
      const kidney = kidneyResult.meshes[0];
      kidney.scaling = new BABYLON.Vector3(0.5, 0.5, 0.5);

      const boundingInfo = kidney.getHierarchyBoundingVectors();
      const center = boundingInfo.min.add(boundingInfo.max).scale(0.5);
      kidney.position = kidney.position.subtract(center);

      kidneyResult.meshes.forEach((mesh) => {
        if (
          mesh.name.toLowerCase().includes("red") ||
          mesh.name.toLowerCase().includes("artery")
        ) {
          arteryMesh = mesh;
        } else if (mesh.getTotalVertices() > 0) {
          kidneyBodyMesh = mesh;
        }
      });

      // Configurar bisturí
      const scalpelRoot = scalpelResult.meshes[0];
      scalpelRoot.setParent(null);

      scalpelMesh = scalpelRoot as BABYLON.AbstractMesh;

      scalpelMesh = scalpelRoot;

      scalpelMesh.rotation = new BABYLON.Vector3(
        BABYLON.Tools.ToRadians(0), // X
        BABYLON.Tools.ToRadians(0), // Y
        BABYLON.Tools.ToRadians(120), // Z
      );

      scalpelMesh.scaling = new BABYLON.Vector3(0.3, 0.5, 0.5);
      scalpelMesh.position = new BABYLON.Vector3(2, 1, 2);
      scalpelMesh.setParent(null);

      let depth = 8; // distancia desde cámara

      scene.onPointerMove = () => {
        if (!scalpelMesh) return;

        const ray = scene.createPickingRay(
          scene.pointerX,
          scene.pointerY,
          BABYLON.Matrix.Identity(),
          camera,
        );

        textX.text = `Coordenada X: ${scalpelMesh.position.x.toFixed(2)}`;
        textY.text = `Coordenada Y: ${scalpelMesh.position.y.toFixed(2)}`;
        textZ.text = `Coordenada Z: ${scalpelMesh.position.z.toFixed(2)}`;

        const newPosition = ray.origin.add(ray.direction.scale(depth));

        // Ajuste del cursor para que esté cerca de la hoja del bisturí
        const offset = new BABYLON.Vector3(0.1, -0.2, 0);

        scalpelMesh.position = newPosition.add(offset);

        cutter.position = scalpelMesh.position.clone();
        cutter.rotation = scalpelMesh.rotation.clone();

        checkCollisions();
      };

      startButton.onPointerUpObservable.add(() => {
        if (simulationStarted) return;

        simulationStarted = true;
        simulationEnded = false;
        camera.panningSensibility = 0;
        camera.detachControl();

        console.log("🚀 Simulación iniciada");
        console.log("🔑 Intentando obtener token para WebSocket...");

        setEvent("START"); // 🔥 ENVÍA ENUM AL BACKEND

        // Iniciar conexión WebSocket
        conectarWebSocket();

        startButton.isVisible = false;
        endButton.isVisible = true;
      });

      endButton.onPointerUpObservable.add(() => {
        if (!simulationStarted || simulationEnded) return;

        simulationEnded = true;
        simulationStarted = false;
        camera.panningSensibility = 0;
        camera.detachControl();

        console.log("Simulación terminada ✅");

        setEvent("FINISH"); // 🔥 ENVÍA ENUM AL BACKEND

        // Cerrar conexión WebSocket
        enviarEvento(
          scalpelMesh?.position.x,
          scalpelMesh?.position.y,
          scalpelMesh?.position.z,
          "FINISH",
        );
        // El cierre real de la conexión y la consulta de la trayectoria
        // ahora ocurren dentro del websocketRef.current.onmessage
        // una vez recibimos el mensaje de status === "SAVED"

        startButton.isVisible = true;
        endButton.isVisible = false;
      });

      scene.onPointerObservable.add((pointerInfo) => {
        if (pointerInfo.type === BABYLON.PointerEventTypes.POINTERDOWN) {
          instrumentActive = true;
        }

        if (pointerInfo.type === BABYLON.PointerEventTypes.POINTERUP) {
          instrumentActive = false;
          // Resetear estados de toque al soltar el ratón para permitir nuevas alertas
          kidneyTouched = false;
        }
      });

      sceneReady = true;
    });

    // Crear el tumor como fragmentos dispersos
    const tumorMaterial = new BABYLON.StandardMaterial("tumorMat", scene);
    tumorMaterial.diffuseColor = new BABYLON.Color3(0.8, 0, 0.2);

    const fragmentCount = 25;
    const radius = 0.6;

    for (let i = 0; i < fragmentCount; i++) {
      const fragment = BABYLON.MeshBuilder.CreateSphere(
        "tumorFragment",
        { diameter: 0.4, segments: 8 },
        scene,
      );

      // Distribuir alrededor de un punto central
      const randomOffset = new BABYLON.Vector3(
        (Math.random() - 0.5) * radius,
        (Math.random() - 0.5) * radius,
        (Math.random() - 0.5) * radius,
      );

      fragment.position = new BABYLON.Vector3(1.2, 0.2, -0.7).add(randomOffset);
      fragment.material = tumorMaterial;

      tumorFragments.push(fragment);
    }

    function checkCollisions() {
      if (!simulationStarted) return;
      if (!sceneReady) return;
      if (!instrumentActive) return;
      if (!cutter || !arteryMesh || !kidneyBodyMesh) return;

      // Se detecta el corte de la arteria
      if (!arteryCut && cutter.intersectsMesh(arteryMesh, true)) {
        arteryCut = true;

        const mat = new BABYLON.StandardMaterial("cut", scene);
        mat.diffuseColor = BABYLON.Color3.Red();
        arteryMesh.material = mat;

        enviarEvento(
          scalpelMesh?.position.x,
          scalpelMesh?.position.y,
          scalpelMesh?.position.z,
          "HEMORRHAGE",
        );
        console.log("Arteria cortada 🔪");
        setEvent("HEMORRHAGE");
      }

      // Se detecta el contacto accidental con el riñón (sano)
      if (!kidneyTouched && cutter.intersectsMesh(kidneyBodyMesh, true)) {
        kidneyTouched = true;
        console.warn("¡Contacto con tejido sano! ⚠️");

        enviarEvento(
          scalpelMesh?.position.x,
          scalpelMesh?.position.y,
          scalpelMesh?.position.z,
          "KIDNEY_TOUCH",
        );
        setEvent("KIDNEY_TOUCH");
      }

      // Se detecta el corte de los fragmentos del tumor
      tumorFragments.forEach((fragment, index) => {
        if (cutter.intersectsMesh(fragment, true)) {
          fragment.dispose();
          tumorFragments.splice(index, 1);

          console.log("Fragmento removido 🧠");
          enviarEvento(
            scalpelMesh?.position.x,
            scalpelMesh?.position.y,
            scalpelMesh?.position.z,
            "TUMOR_REMOVAL",
          );
          setEvent("TUMOR_REMOVAL");

          if (tumorFragments.length === 0) {
            console.log("Tumor completamente removido ✅");
            setEvent("FINISH");
          }
        }
      });
    }

    engine.runRenderLoop(() => {
      scene.render();
    });

    return () => {
      engine.dispose();
    };
  }, [setEvent]);
  return (
    <div style={{ position: "relative", width: "100%", height: "100vh" }}>
      <canvas
        ref={canvasRef}
        style={{ width: "100%", height: "100vh", display: "block" }}
      />

      {/* Contenedor de botones */}
      <div className="absolute top-6 left-6 z-50 flex gap-3">
        {/* Botón volver */}
        <Link href="/">
          <Button
            variant="outline"
            className="bg-white/90 backdrop-blur-md 
                     text-slate-700 hover:text-black
                     shadow-lg rounded-xl"
          >
            ← Volver
          </Button>
        </Link>

        {/* Botón reiniciar */}
        <Button
          onClick={reiniciarSimulacion}
          className="bg-blue-600 hover:bg-blue-700 
                   text-white shadow-lg rounded-xl"
        >
          ⟳ Reiniciar
        </Button>
      </div>
    </div>
  );
}

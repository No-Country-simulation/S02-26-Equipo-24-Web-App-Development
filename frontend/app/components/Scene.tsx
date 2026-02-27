"use client";

import { useEffect, useRef } from "react";
import * as BABYLON from "@babylonjs/core";
import * as GUI from "@babylonjs/gui";
import "@babylonjs/loaders";
import { useSurgeryStore } from "../store/surgeryStore";
import Link from "next/link";
import { Button } from "@/app/components/ui/button";

export default function BabylonScene() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const setEvent = useSurgeryStore((s) => s.setEvent);

  useEffect(() => {
    if (!canvasRef.current) return;

    const engine = new BABYLON.Engine(canvasRef.current, true);
    const scene = new BABYLON.Scene(engine);
    scene.clearColor = new BABYLON.Color4(0.8, 0.9, 1, 1);

    const advancedTexture = GUI.AdvancedDynamicTexture.CreateFullscreenUI("UI");

    const coordPanel = new GUI.Rectangle();
    coordPanel.width = "280px";
    coordPanel.height = "160px";
    coordPanel.cornerRadius = 10;
    coordPanel.color = "black";
    coordPanel.thickness = 2;
    coordPanel.background = "rgba(245, 187, 187, 0.8)";
    coordPanel.horizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_LEFT;
    coordPanel.verticalAlignment = GUI.Control.VERTICAL_ALIGNMENT_TOP;
    coordPanel.paddingRight = "20px";

    advancedTexture.addControl(coordPanel);

    const stack = new GUI.StackPanel();
    stack.paddingTop = "15px";
    stack.paddingLeft = "15px";
    stack.paddingRight = "15px";

    coordPanel.addControl(stack);

    const title = new GUI.TextBlock();
    title.text = "Coordenadas del Instrumento:";
    title.color = "black";
    title.fontSize = 16;
    title.height = "30px";
    title.paddingBottom = "10px";
    title.textHorizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_LEFT;

    stack.addControl(title);

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

    camera.lowerRadiusLimit = 6;
    camera.upperRadiusLimit = 20;

    // Luz quirúrgica
    new BABYLON.HemisphericLight("light", new BABYLON.Vector3(0, 1, 0), scene);

    let scalpelMesh: BABYLON.AbstractMesh | null = null;
    const tumorFragments: BABYLON.Mesh[] = [];
    let arteryMesh: BABYLON.AbstractMesh | null = null;

    let sceneReady = false;
    let instrumentActive = false;
    let arteryCut = false;

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
        if (mesh.name.toLowerCase().includes("red")) {
          arteryMesh = mesh;
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

      scene.onPointerObservable.add((pointerInfo) => {
        if (pointerInfo.type === BABYLON.PointerEventTypes.POINTERDOWN) {
          instrumentActive = true;
        }

        if (pointerInfo.type === BABYLON.PointerEventTypes.POINTERUP) {
          instrumentActive = false;
        }

        if (pointerInfo.type === BABYLON.PointerEventTypes.POINTERWHEEL) {
          const wheelEvent = pointerInfo.event as WheelEvent;
          depth += wheelEvent.deltaY * 0.01;
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
      if (!sceneReady) return;
      if (!instrumentActive) return;
      if (!cutter || !arteryMesh) return;

      // Se detecta el corte de la arteria
      if (!arteryCut && cutter.intersectsMesh(arteryMesh, true)) {
        arteryCut = true;

        const mat = new BABYLON.StandardMaterial("cut", scene);
        mat.diffuseColor = BABYLON.Color3.Red();
        arteryMesh.material = mat;

        console.log("Arteria cortada 🔪");
        setEvent("HEMORRHAGE");
      }

      // Se detecta el corte de los fragmentos del tumor
      tumorFragments.forEach((fragment, index) => {
        if (cutter.intersectsMesh(fragment, true)) {
          fragment.dispose();
          tumorFragments.splice(index, 1);

          console.log("Fragmento removido 🧠");
          setEvent("TUMOR_TOUCH");

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
    <div>
      <canvas ref={canvasRef} style={{ width: "100%", height: "100vh" }} />
      <Link href={"/"}>
        <Button
          variant="outline"
          className="mt-4 w-full text-slate-600 hover:text-slate-900"
        >
          Volver al Inicio
        </Button>
      </Link>
    </div>
  );
}

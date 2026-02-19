"use client";

import { useEffect, useRef } from "react";
import * as BABYLON from "@babylonjs/core";
import "@babylonjs/loaders";
import { useSurgeryStore } from "../store/surgeryStore";
import Link from "next/link";
import { Button } from "@/components/ui/button";

export default function BabylonScene() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const setEvent = useSurgeryStore((s) => s.setEvent);

  useEffect(() => {
    if (!canvasRef.current) return;

    const engine = new BABYLON.Engine(canvasRef.current, true);
    const scene = new BABYLON.Scene(engine);
    scene.clearColor = new BABYLON.Color4(0.8, 0.9, 1, 1);

    // Cámara laparoscópica
    const camera = new BABYLON.ArcRotateCamera(
      "camera",
      Math.PI / 2,
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

    let scalpelMesh: BABYLON.Mesh | null = null;
    let tumorMesh: BABYLON.Mesh | null = null;
    let arteryMesh: BABYLON.AbstractMesh | null = null;

    let instrumentActive = false;

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
      const root = kidneyResult.meshes[0];
      root.scaling = new BABYLON.Vector3(0.5, 0.5, 0.5);

      const boundingInfo = root.getHierarchyBoundingVectors();
      const center = boundingInfo.min.add(boundingInfo.max).scale(0.5);
      root.position = root.position.subtract(center);

      kidneyResult.meshes.forEach((mesh) => {
        if (mesh.name.toLowerCase().includes("red")) {
          arteryMesh = mesh;
        }
      });

      const realMesh = scalpelResult.meshes.find(
        (m) => m instanceof BABYLON.Mesh && m.getTotalVertices() > 0,
      ) as BABYLON.Mesh;

      scalpelMesh = realMesh;
      scalpelMesh.scaling = new BABYLON.Vector3(0.3, 0.5, 0.5);
      scalpelMesh.position = new BABYLON.Vector3(2, 1, 2);
      scalpelMesh.setParent(null);
      scalpelMesh.bakeCurrentTransformIntoVertices();

      let depth = 8; // distancia desde cámara

      scene.onPointerMove = () => {
        if (!scalpelMesh) return;

        const ray = scene.createPickingRay(
          scene.pointerX,
          scene.pointerY,
          BABYLON.Matrix.Identity(),
          camera,
        );

        const newPosition = ray.origin.add(ray.direction.scale(depth));

        scalpelMesh.position = newPosition;

        cutter.position = scalpelMesh.position;
        cutter.rotation = scalpelMesh.rotation;
      };
    });

    // Crear tumor como esfera roja
    tumorMesh = BABYLON.MeshBuilder.CreateSphere(
      "tumor",
      { diameter: 1.2, segments: 16 },
      scene,
    );

    tumorMesh.position = new BABYLON.Vector3(1, -0.2, -0.3);

    const tumorMaterial = new BABYLON.StandardMaterial("tumorMat", scene);
    tumorMaterial.diffuseColor = new BABYLON.Color3(0.8, 0, 0.2);
    tumorMesh.material = tumorMaterial;

    engine.runRenderLoop(() => {
      scene.render();
    });

    return () => {
      engine.dispose();
    };
  }, []);
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
  </div>);
}

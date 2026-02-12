import React, { Suspense, useEffect, useMemo, useRef, useState } from 'react'
import { Canvas, useFrame, useLoader, useThree } from '@react-three/fiber'
import { OrbitControls, Line, Html } from '@react-three/drei'
import * as THREE from 'three'
import { STLLoader } from 'three/examples/jsm/loaders/STLLoader'

const TOOL_LIMITS = {
  min: new THREE.Vector3(-2.2, -1.6, -2.2),
  max: new THREE.Vector3(2.2, 2.0, 2.2)
}

function useKeyboard() {
  const [keys, setKeys] = useState({})

  useEffect(() => {
    const onDown = (event) => {
      setKeys((prev) => ({ ...prev, [event.key.toLowerCase()]: true }))
    }
    const onUp = (event) => {
      setKeys((prev) => ({ ...prev, [event.key.toLowerCase()]: false }))
    }

    window.addEventListener('keydown', onDown)
    window.addEventListener('keyup', onUp)
    return () => {
      window.removeEventListener('keydown', onDown)
      window.removeEventListener('keyup', onUp)
    }
  }, [])

  return keys
}

function KidneyModel({ onBounds, active }) {
  const meshRef = useRef(null)
  const geometry = useLoader(STLLoader, '/models/kidney.stl')

  useMemo(() => {
    geometry.center()
    geometry.computeVertexNormals()
    const box = new THREE.Box3().setFromBufferAttribute(geometry.getAttribute('position'))
    const size = new THREE.Vector3()
    box.getSize(size)
    const maxAxis = Math.max(size.x, size.y, size.z)
    const scale = 3 / maxAxis
    geometry.scale(scale, scale, scale)
    geometry.computeBoundingBox()
    if (geometry.boundingBox) {
      const scaledBox = geometry.boundingBox.clone()
      onBounds?.(scaledBox)
    }
  }, [geometry, onBounds])

  useFrame(({ clock }) => {
    if (active) return
    const t = clock.getElapsedTime()
    if (meshRef.current) {
      meshRef.current.rotation.y = t * 0.2
      meshRef.current.rotation.z = Math.sin(t * 0.4) * 0.05
    }
  })

  return (
    <mesh ref={meshRef} geometry={geometry} castShadow receiveShadow>
      <meshPhysicalMaterial
        color="#b84848"
        roughness={0.35}
        metalness={0.05}
        clearcoat={0.45}
        clearcoatRoughness={0.2}
        transmission={0.05}
        thickness={0.8}
      />
    </mesh>
  )
}

function RobotTool({ active, speed, onPosition }) {
  const toolRef = useRef(null)
  const keys = useKeyboard()
  const pointer = useThree((state) => state.pointer)
  const viewport = useThree((state) => state.viewport)

  useFrame((_, delta) => {
    if (!toolRef.current) return

    if (active) {
      // Mouse controls X/Y. Q/E adjust depth.
      const target = new THREE.Vector3(
        pointer.x * (viewport.width / 3),
        pointer.y * (viewport.height / 3),
        toolRef.current.position.z
      )

      const zMove = (keys['e'] ? 1 : 0) - (keys['q'] ? 1 : 0)
      target.z += zMove * speed * delta * 2

      toolRef.current.position.lerp(target, 0.2)

      toolRef.current.position.x = THREE.MathUtils.clamp(
        toolRef.current.position.x,
        TOOL_LIMITS.min.x,
        TOOL_LIMITS.max.x
      )
      toolRef.current.position.y = THREE.MathUtils.clamp(
        toolRef.current.position.y,
        TOOL_LIMITS.min.y,
        TOOL_LIMITS.max.y
      )
      toolRef.current.position.z = THREE.MathUtils.clamp(
        toolRef.current.position.z,
        TOOL_LIMITS.min.z,
        TOOL_LIMITS.max.z
      )
    }

    onPosition?.(toolRef.current.position.clone(), delta)
  })

  return (
    <group ref={toolRef} position={[0, 0, -1.2]}>
      <mesh>
        <cylinderGeometry args={[0.05, 0.05, 2.5, 24]} />
        <meshStandardMaterial color="#d8f0ff" roughness={0.2} metalness={0.8} />
      </mesh>
      <mesh position={[0, -1.35, 0]}>
        <sphereGeometry args={[0.12, 24, 24]} />
        <meshStandardMaterial color="#c7e3ff" roughness={0.2} metalness={0.6} />
      </mesh>
    </group>
  )
}

function TrajectoryLine({ points }) {
  if (!points.length) return null
  return <Line points={points} color="#64f2b4" lineWidth={2} />
}

function TargetSphere({ center, radius, color, opacity }) {
  if (!center) return null
  return (
    <mesh position={center}>
      <sphereGeometry args={[radius, 24, 24]} />
      <meshStandardMaterial color={color} transparent opacity={opacity} />
    </mesh>
  )
}

function Scene({
  active,
  speed,
  feedback,
  onToolPosition,
  kidneyBounds,
  target,
  danger,
  pathPoints
}) {
  const targetOpacity = THREE.MathUtils.lerp(0.2, 0.55, feedback / 100)
  const dangerOpacity = THREE.MathUtils.lerp(0.15, 0.4, feedback / 100)
  return (
    <>
      <ambientLight intensity={0.35} />
      <hemisphereLight intensity={0.35} groundColor="#0b1b2d" color="#d6e6ff" />
      <directionalLight position={[4, 6, 3]} intensity={1.3} castShadow />
      <pointLight position={[-4, -3, -2]} intensity={0.6} color="#7fd0ff" />

      <group position={[0, 0, 0]}>
        <Suspense fallback={<Html center className="loading">Cargando modelo...</Html>}>
          <KidneyModel onBounds={kidneyBounds} active={active} />
        </Suspense>
        <RobotTool active={active} speed={speed} onPosition={onToolPosition} />
        <TrajectoryLine points={pathPoints} />
        <TargetSphere center={target?.center} radius={target?.radius ?? 0} color="#64f2b4" opacity={targetOpacity} />
        <TargetSphere center={danger?.center} radius={danger?.radius ?? 0} color="#ff6b6b" opacity={dangerOpacity} />
      </group>

      <mesh rotation={[-Math.PI / 2, 0, 0]} position={[0, -3.2, 0]} receiveShadow>
        <circleGeometry args={[8, 64]} />
        <meshStandardMaterial color="#0c1320" roughness={0.9} />
      </mesh>

      <OrbitControls enableZoom enableRotate={!active} minDistance={4} maxDistance={10} />
    </>
  )
}

function formatTime(seconds) {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

export default function App() {
  const [scenario, setScenario] = useState('Basico')
  const [isRunning, setIsRunning] = useState(false)
  const [speed, setSpeed] = useState(1.2)
  const [precision, setPrecision] = useState(75)
  const [feedback, setFeedback] = useState(80)
  const [metrics, setMetrics] = useState({
    time: 0,
    path: 0,
    errors: 0,
    collisions: 0,
    precision: 0
  })
  const [pathPoints, setPathPoints] = useState([])
  const [kidneyBox, setKidneyBox] = useState(null)

  const lastPosRef = useRef(null)
  const inDangerRef = useRef(false)
  const inCollisionRef = useRef(false)
  const distanceSumRef = useRef(0)
  const distanceCountRef = useRef(0)

  const target = useMemo(() => {
    if (!kidneyBox) return null
    const center = new THREE.Vector3()
    kidneyBox.getCenter(center)
    const radius = THREE.MathUtils.lerp(0.9, 0.3, precision / 100)
    return {
      center: center.clone().add(new THREE.Vector3(0.6, 0.2, 0.2)),
      radius
    }
  }, [kidneyBox, precision])

  const danger = useMemo(() => {
    if (!kidneyBox) return null
    const center = new THREE.Vector3()
    kidneyBox.getCenter(center)
    return {
      center: center.clone().add(new THREE.Vector3(-0.7, -0.1, -0.2)),
      radius: 0.45
    }
  }, [kidneyBox])

  const handleToolPosition = (position, delta) => {
    if (!position) return

    if (isRunning) {
      setMetrics((prev) => {
        const next = { ...prev }
        next.time += delta
        if (lastPosRef.current) {
          next.path += lastPosRef.current.distanceTo(position)
        }

        if (target) {
          const dist = position.distanceTo(target.center)
          distanceSumRef.current += dist
          distanceCountRef.current += 1
          const avg = distanceSumRef.current / Math.max(1, distanceCountRef.current)
          next.precision = Math.max(0, 100 - avg * 40)
        }

        if (danger) {
          const inDanger = position.distanceTo(danger.center) <= danger.radius
          if (inDanger && !inDangerRef.current) {
            next.errors += 1
          }
          inDangerRef.current = inDanger
        }

        if (kidneyBox) {
          const inCollision = kidneyBox.containsPoint(position)
          if (inCollision && !inCollisionRef.current) {
            next.collisions += 1
          }
          inCollisionRef.current = inCollision
        }

        return next
      })

      setPathPoints((prev) => {
        const next = prev.length > 250 ? prev.slice(-250) : [...prev]
        next.push(position.clone())
        return next
      })
    }

    lastPosRef.current = position
  }

  const handleStartStop = () => {
    if (isRunning) {
      setIsRunning(false)
      return
    }
    lastPosRef.current = null
    inDangerRef.current = false
    inCollisionRef.current = false
    distanceSumRef.current = 0
    distanceCountRef.current = 0
    setPathPoints([])
    setMetrics({ time: 0, path: 0, errors: 0, collisions: 0, precision: 0 })
    setIsRunning(true)
  }

  const handleExport = () => {
    const payload = {
      scenario,
      metrics,
      precisionSetting: precision,
      speedSetting: speed,
      feedbackSetting: feedback,
      timestamp: new Date().toISOString()
    }
    const blob = new Blob([JSON.stringify(payload, null, 2)], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `justina-metricas-${Date.now()}.json`
    document.body.appendChild(a)
    a.click()
    a.remove()
    URL.revokeObjectURL(url)
  }

  return (
    <div className="app">
      <header className="header">
        <div className="brand">
          <span className="brand-dot" />
          <div>
            <h1>Justina</h1>
            <p>Simulacion renal minima invasiva</p>
          </div>
        </div>
        <div className="status">
          <span>Estado</span>
          <strong>{isRunning ? 'Sesion activa' : 'En espera'}</strong>
        </div>
      </header>

      <main className="content">
        <section className="panel left">
          <h2>Escenarios</h2>
          <div className="chips">
            {['Basico', 'Intermedio', 'Avanzado'].map((level) => (
              <button
                key={level}
                type="button"
                className={scenario === level ? 'chip active' : 'chip'}
                onClick={() => setScenario(level)}
              >
                {level}
              </button>
            ))}
          </div>
          <div className="metrics">
            <div>
              <span>Tiempo</span>
              <strong>{formatTime(metrics.time)}</strong>
            </div>
            <div>
              <span>Trayectoria</span>
              <strong>{metrics.path.toFixed(2)} m</strong>
            </div>
            <div>
              <span>Precision</span>
              <strong>{metrics.precision.toFixed(1)}%</strong>
            </div>
            <div>
              <span>Errores</span>
              <strong>{metrics.errors}</strong>
            </div>
            <div>
              <span>Colisiones</span>
              <strong>{metrics.collisions}</strong>
            </div>
          </div>
          <div className="steps">
            <div className="step">1. Inicia sesion</div>
            <div className="step">2. Mueve el brazo con el mouse</div>
            <div className="step">3. Ajusta profundidad con Q/E</div>
            <div className="step">4. Evita la zona roja</div>
            <div className="step">5. Exporta metricas</div>
          </div>
          <p className="note">
            La esfera verde es el objetivo y la roja es zona de riesgo.
          </p>
        </section>

        <section className="stage">
          <Canvas shadows camera={{ position: [0, 2.5, 7], fov: 45 }}>
            <Scene
              active={isRunning}
              speed={speed}
              feedback={feedback}
              onToolPosition={handleToolPosition}
              kidneyBounds={setKidneyBox}
              target={target}
              danger={danger}
              pathPoints={pathPoints}
            />
          </Canvas>
        </section>

        <section className="panel right">
          <h2>Controles</h2>
          <div className="control">
            <label htmlFor="speed">Velocidad del brazo</label>
            <input
              id="speed"
              type="range"
              min="10"
              max="200"
              value={speed * 100}
              onChange={(e) => setSpeed(Number(e.target.value) / 100)}
            />
          </div>
          <div className="control">
            <label htmlFor="precision">Precision requerida</label>
            <input
              id="precision"
              type="range"
              min="0"
              max="100"
              value={precision}
              onChange={(e) => setPrecision(Number(e.target.value))}
            />
          </div>
          <div className="control">
            <label htmlFor="feedback">Feedback visual</label>
            <input
              id="feedback"
              type="range"
              min="0"
              max="100"
              value={feedback}
              onChange={(e) => setFeedback(Number(e.target.value))}
            />
          </div>
          <button className="primary" type="button" onClick={handleStartStop}>
            {isRunning ? 'Detener sesion' : 'Iniciar sesion'}
          </button>
          <button className="ghost" type="button" onClick={handleExport}>
            Exportar metricas
          </button>
        </section>
      </main>

      <footer className="footer">
        <span>
          Prototipo digital para validacion temprana con medicos y decisores. Modelo 3D:
          <a href="https://3d.nih.gov/entries/3DPX-020968" target="_blank" rel="noreferrer">
            NIH 3D (CC BY 4.0)
          </a>
        </span>
      </footer>
    </div>
  )
}

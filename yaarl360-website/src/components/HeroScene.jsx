import { useRef, useEffect } from 'react'
import * as THREE from 'three'

export default function HeroScene() {
  const mountRef = useRef(null)

  useEffect(() => {
    const el = mountRef.current
    if (!el) return

    const noMotion = window.matchMedia('(prefers-reduced-motion: reduce)')
    let reduceMotion = noMotion.matches
    const motionHandler = (e) => { reduceMotion = e.matches }
    noMotion.addEventListener('change', motionHandler)

    // Renderer
    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true })
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
    renderer.setSize(el.clientWidth, el.clientHeight)
    renderer.shadowMap.enabled = true
    renderer.shadowMap.type = THREE.PCFSoftShadowMap
    el.appendChild(renderer.domElement)

    // Scene & camera
    const scene = new THREE.Scene()
    const camera = new THREE.PerspectiveCamera(50, el.clientWidth / el.clientHeight, 0.1, 100)
    camera.position.set(0, 3, 8)
    camera.lookAt(0, 0, 0)

    // Lights
    const ambient = new THREE.AmbientLight(0xffffff, 0.6)
    scene.add(ambient)

    const dirLight = new THREE.DirectionalLight(0xffffff, 1.2)
    dirLight.position.set(5, 8, 5)
    dirLight.castShadow = true
    dirLight.shadow.mapSize.set(1024, 1024)
    scene.add(dirLight)

    const fillLight = new THREE.DirectionalLight(0x3B82F6, 0.4)
    fillLight.position.set(-5, 2, -3)
    scene.add(fillLight)

    // Island base (green rounded cylinder)
    const islandGeo = new THREE.CylinderGeometry(2.8, 2.2, 0.6, 32)
    const islandMat = new THREE.MeshLambertMaterial({ color: 0x22C55E })
    const island = new THREE.Mesh(islandGeo, islandMat)
    island.position.y = -0.3
    island.receiveShadow = true
    scene.add(island)

    // Island bottom (brown earth)
    const earthGeo = new THREE.CylinderGeometry(2.2, 1.8, 0.5, 32)
    const earthMat = new THREE.MeshLambertMaterial({ color: 0x92400E })
    const earth = new THREE.Mesh(earthGeo, earthMat)
    earth.position.y = -0.85
    scene.add(earth)

    // House body
    const bodyGeo = new THREE.BoxGeometry(1.8, 1.4, 1.4)
    const bodyMat = new THREE.MeshLambertMaterial({ color: 0xFEF3C7 })
    const body = new THREE.Mesh(bodyGeo, bodyMat)
    body.position.set(0, 0.7, 0)
    body.castShadow = true
    body.receiveShadow = true
    scene.add(body)

    // Roof (pyramid)
    const roofGeo = new THREE.ConeGeometry(1.5, 0.9, 4)
    const roofMat = new THREE.MeshLambertMaterial({ color: 0x1E40AF })
    const roof = new THREE.Mesh(roofGeo, roofMat)
    roof.position.set(0, 1.85, 0)
    roof.rotation.y = Math.PI / 4
    roof.castShadow = true
    scene.add(roof)

    // Door
    const doorGeo = new THREE.BoxGeometry(0.38, 0.6, 0.05)
    const doorMat = new THREE.MeshLambertMaterial({ color: 0xEA580C })
    const door = new THREE.Mesh(doorGeo, doorMat)
    door.position.set(0, 0.3, 0.73)
    scene.add(door)

    // Windows x2
    const winGeo = new THREE.BoxGeometry(0.35, 0.35, 0.05)
    const winMat = new THREE.MeshLambertMaterial({ color: 0x93C5FD })
    const win1 = new THREE.Mesh(winGeo, winMat)
    win1.position.set(-0.55, 0.75, 0.73)
    scene.add(win1)
    const win2 = win1.clone()
    win2.position.set(0.55, 0.75, 0.73)
    scene.add(win2)

    // Trees (x3 around island)
    function makeTree(x, z) {
      const trunkGeo = new THREE.CylinderGeometry(0.06, 0.08, 0.4, 8)
      const trunk = new THREE.Mesh(trunkGeo, new THREE.MeshLambertMaterial({ color: 0x92400E }))
      trunk.position.set(x, 0.2, z)
      scene.add(trunk)
      const topGeo = new THREE.ConeGeometry(0.28, 0.6, 8)
      const top = new THREE.Mesh(topGeo, new THREE.MeshLambertMaterial({ color: 0x16A34A }))
      top.position.set(x, 0.7, z)
      scene.add(top)
    }
    makeTree(-1.8, 1.2)
    makeTree(1.9, 0.8)
    makeTree(-1.0, -1.8)

    // Floating particles
    const particleCount = 60
    const particleGeo = new THREE.BufferGeometry()
    const positions = new Float32Array(particleCount * 3)
    for (let i = 0; i < particleCount; i++) {
      positions[i * 3] = (Math.random() - 0.5) * 12
      positions[i * 3 + 1] = (Math.random() - 0.5) * 8
      positions[i * 3 + 2] = (Math.random() - 0.5) * 8 - 2
    }
    particleGeo.setAttribute('position', new THREE.BufferAttribute(positions, 3))
    const particleMat = new THREE.PointsMaterial({ color: 0x93C5FD, size: 0.06, transparent: true, opacity: 0.7 })
    const particles = new THREE.Points(particleGeo, particleMat)
    scene.add(particles)

    // Animation loop
    let raf
    let t = 0
    const group = new THREE.Group()
    group.add(island, earth, body, roof, door, win1, win2)
    scene.add(group)

    function animate() {
      raf = requestAnimationFrame(animate)
      t += 0.01
      if (!reduceMotion) {
        group.rotation.y = t * 0.25
        group.position.y = Math.sin(t * 0.8) * 0.12
        particles.rotation.y = t * 0.04
      }
      renderer.render(scene, camera)
    }
    animate()

    // Resize
    const onResize = () => {
      const w = el.clientWidth, h = el.clientHeight
      camera.aspect = w / h
      camera.updateProjectionMatrix()
      renderer.setSize(w, h)
    }
    window.addEventListener('resize', onResize)

    return () => {
      cancelAnimationFrame(raf)
      window.removeEventListener('resize', onResize)
      noMotion.removeEventListener('change', motionHandler)
      renderer.dispose()
      el.removeChild(renderer.domElement)
    }
  }, [])

  return (
    <div
      ref={mountRef}
      aria-hidden="true"
      style={{ width: '100%', height: '100%', position: 'absolute', inset: 0 }}
    />
  )
}

import { useRef, useEffect } from 'react'
import * as THREE from 'three'

const SERVICE_CONFIGS = {
  plumbing: {
    color: 0x3B82F6,
    build: (scene) => {
      // Pipe
      const pipe = new THREE.Mesh(
        new THREE.CylinderGeometry(0.12, 0.12, 1.4, 16),
        new THREE.MeshLambertMaterial({ color: 0x6B7280 })
      )
      scene.add(pipe)
      const elbow = new THREE.Mesh(
        new THREE.TorusGeometry(0.3, 0.12, 12, 16, Math.PI / 2),
        new THREE.MeshLambertMaterial({ color: 0x3B82F6 })
      )
      elbow.position.set(0.3, 0.7, 0)
      elbow.rotation.z = Math.PI / 2
      scene.add(elbow)
      const drop = new THREE.Mesh(
        new THREE.SphereGeometry(0.14, 12, 12),
        new THREE.MeshLambertMaterial({ color: 0x93C5FD })
      )
      drop.position.set(0.3, 0.4, 0)
      scene.add(drop)
    }
  },
  electrical: {
    color: 0xEAB308,
    build: (scene) => {
      const bulb = new THREE.Mesh(
        new THREE.SphereGeometry(0.5, 16, 16),
        new THREE.MeshLambertMaterial({ color: 0xFEF08A, emissive: 0xFEF08A, emissiveIntensity: 0.3 })
      )
      scene.add(bulb)
      const base = new THREE.Mesh(
        new THREE.CylinderGeometry(0.18, 0.22, 0.3, 12),
        new THREE.MeshLambertMaterial({ color: 0x9CA3AF })
      )
      base.position.y = -0.6
      scene.add(base)
      // Bolt
      const boltShape = new THREE.Shape()
      boltShape.moveTo(0, 0.5)
      boltShape.lineTo(0.25, 0)
      boltShape.lineTo(0.1, 0)
      boltShape.lineTo(0.3, -0.5)
      boltShape.lineTo(-0.05, 0)
      boltShape.lineTo(0.1, 0)
      boltShape.lineTo(-0.15, 0.5)
      const boltGeo = new THREE.ShapeGeometry(boltShape)
      const bolt = new THREE.Mesh(boltGeo, new THREE.MeshLambertMaterial({ color: 0xEAB308 }))
      bolt.position.set(0.7, 0.1, 0.2)
      scene.add(bolt)
    }
  },
  cleaning: {
    color: 0x22C55E,
    build: (scene) => {
      const bucket = new THREE.Mesh(
        new THREE.CylinderGeometry(0.35, 0.28, 0.7, 16, 1, true),
        new THREE.MeshLambertMaterial({ color: 0x3B82F6, side: THREE.DoubleSide })
      )
      bucket.position.y = -0.3
      scene.add(bucket)
      const rim = new THREE.Mesh(
        new THREE.TorusGeometry(0.35, 0.04, 8, 24),
        new THREE.MeshLambertMaterial({ color: 0x1D4ED8 })
      )
      rim.position.y = 0.05
      scene.add(rim)
      // Bubbles
      ;[[-0.3, 0.5, 0], [0.2, 0.7, 0.1], [0, 0.9, -0.1]].forEach(([x, y, z]) => {
        const b = new THREE.Mesh(
          new THREE.SphereGeometry(0.1, 8, 8),
          new THREE.MeshLambertMaterial({ color: 0xBAE6FD, transparent: true, opacity: 0.8 })
        )
        b.position.set(x, y, z)
        scene.add(b)
      })
    }
  },
  ac: {
    color: 0x06B6D4,
    build: (scene) => {
      const unit = new THREE.Mesh(
        new THREE.BoxGeometry(1.4, 0.7, 0.5),
        new THREE.MeshLambertMaterial({ color: 0xF1F5F9 })
      )
      scene.add(unit)
      const vent = new THREE.Mesh(
        new THREE.BoxGeometry(1.2, 0.08, 0.05),
        new THREE.MeshLambertMaterial({ color: 0x06B6D4 })
      )
      for (let i = 0; i < 4; i++) {
        const v = vent.clone()
        v.position.set(0, -0.15 + i * 0.1, 0.28)
        scene.add(v)
      }
      // Snowflake-like particles
      ;[[0.5,0.8,0],[0,-0.2,0],[-.5,.5,0]].forEach(([x,y,z]) => {
        const flake = new THREE.Mesh(
          new THREE.SphereGeometry(0.07, 6, 6),
          new THREE.MeshLambertMaterial({ color: 0xBAE6FD })
        )
        flake.position.set(x, y, z)
        scene.add(flake)
      })
    }
  },
  painting: {
    color: 0xA855F7,
    build: (scene) => {
      const roller = new THREE.Mesh(
        new THREE.CylinderGeometry(0.18, 0.18, 0.8, 16),
        new THREE.MeshLambertMaterial({ color: 0xA855F7 })
      )
      roller.rotation.z = Math.PI / 2
      roller.position.y = 0.2
      scene.add(roller)
      const handle = new THREE.Mesh(
        new THREE.CylinderGeometry(0.04, 0.04, 0.9, 8),
        new THREE.MeshLambertMaterial({ color: 0x9CA3AF })
      )
      handle.rotation.z = -Math.PI / 4
      handle.position.set(-0.5, -0.2, 0)
      scene.add(handle)
      // Paint drip
      ;[[-0.2, -0.5], [0, -0.6], [0.2, -0.5]].forEach(([x, y]) => {
        const drip = new THREE.Mesh(
          new THREE.CapsuleGeometry(0.05, 0.15, 4, 8),
          new THREE.MeshLambertMaterial({ color: 0xA855F7 })
        )
        drip.position.set(x, y, 0)
        scene.add(drip)
      })
    }
  },
  handyman: {
    color: 0xF59E0B,
    build: (scene) => {
      // Wrench
      const handle2 = new THREE.Mesh(
        new THREE.CapsuleGeometry(0.1, 0.9, 6, 12),
        new THREE.MeshLambertMaterial({ color: 0x6B7280 })
      )
      handle2.rotation.z = Math.PI / 4
      scene.add(handle2)
      const head = new THREE.Mesh(
        new THREE.TorusGeometry(0.2, 0.07, 8, 20, Math.PI * 1.2),
        new THREE.MeshLambertMaterial({ color: 0xF59E0B })
      )
      head.position.set(0.5, 0.5, 0)
      head.rotation.z = -0.5
      scene.add(head)
      const screw = new THREE.Mesh(
        new THREE.CylinderGeometry(0.12, 0.08, 0.3, 6),
        new THREE.MeshLambertMaterial({ color: 0xD97706 })
      )
      screw.position.set(-0.4, -0.6, 0)
      scene.add(screw)
    }
  },
  garden: {
    color: 0x16A34A,
    build: (scene) => {
      // Lawn mower-ish shape
      const body2 = new THREE.Mesh(
        new THREE.BoxGeometry(1, 0.5, 0.7),
        new THREE.MeshLambertMaterial({ color: 0x16A34A })
      )
      body2.position.y = 0.1
      scene.add(body2)
      ;[-0.35, 0.35].forEach(x => {
        ;[-0.25, 0.25].forEach(z => {
          const wheel = new THREE.Mesh(
            new THREE.CylinderGeometry(0.15, 0.15, 0.08, 16),
            new THREE.MeshLambertMaterial({ color: 0x374151 })
          )
          wheel.rotation.z = Math.PI / 2
          wheel.position.set(x, -0.2, z)
          scene.add(wheel)
        })
      })
      // Leaf
      const leaf = new THREE.Mesh(
        new THREE.SphereGeometry(0.2, 8, 8),
        new THREE.MeshLambertMaterial({ color: 0x4ADE80 })
      )
      leaf.scale.set(1, 0.5, 1.5)
      leaf.position.set(0.5, 0.6, 0)
      scene.add(leaf)
    }
  },
  pest: {
    color: 0xEF4444,
    build: (scene) => {
      // Spray bottle
      const bottle = new THREE.Mesh(
        new THREE.CylinderGeometry(0.22, 0.28, 0.9, 16),
        new THREE.MeshLambertMaterial({ color: 0xEF4444 })
      )
      bottle.position.y = -0.2
      scene.add(bottle)
      const nozzle = new THREE.Mesh(
        new THREE.CylinderGeometry(0.06, 0.08, 0.4, 8),
        new THREE.MeshLambertMaterial({ color: 0x374151 })
      )
      nozzle.rotation.z = Math.PI / 2
      nozzle.position.set(0.4, 0.35, 0)
      scene.add(nozzle)
      // spray dots
      ;[[0.7,0.4],[0.85,0.5],[0.75,0.6]].forEach(([x,y]) => {
        const dot = new THREE.Mesh(
          new THREE.SphereGeometry(0.04, 6, 6),
          new THREE.MeshLambertMaterial({ color: 0xFCA5A5, transparent: true, opacity: 0.7 })
        )
        dot.position.set(x, y, 0)
        scene.add(dot)
      })
    }
  },
}

export default function ServiceCard3D({ service }) {
  const mountRef = useRef(null)
  const config = SERVICE_CONFIGS[service] || SERVICE_CONFIGS.plumbing

  useEffect(() => {
    const el = mountRef.current
    if (!el) return

    const noMotion = window.matchMedia('(prefers-reduced-motion: reduce)')
    let reduceMotion = noMotion.matches
    const mh = (e) => { reduceMotion = e.matches }
    noMotion.addEventListener('change', mh)

    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true })
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
    renderer.setSize(el.clientWidth, el.clientHeight)
    el.appendChild(renderer.domElement)

    const scene = new THREE.Scene()
    const camera = new THREE.PerspectiveCamera(50, 1, 0.1, 50)
    camera.position.set(0, 0.5, 3.5)
    camera.lookAt(0, 0, 0)

    const ambient = new THREE.AmbientLight(0xffffff, 0.8)
    scene.add(ambient)
    const dir = new THREE.DirectionalLight(0xffffff, 1)
    dir.position.set(3, 5, 3)
    scene.add(dir)
    const fill = new THREE.DirectionalLight(config.color, 0.5)
    fill.position.set(-3, -1, -2)
    scene.add(fill)

    const group = new THREE.Group()
    config.build(group)
    scene.add(group)

    let raf, t = 0
    function animate() {
      raf = requestAnimationFrame(animate)
      t += 0.016
      if (!reduceMotion) {
        group.rotation.y = t * 0.6
        group.position.y = Math.sin(t * 1.2) * 0.06
      }
      renderer.render(scene, camera)
    }
    animate()

    return () => {
      cancelAnimationFrame(raf)
      noMotion.removeEventListener('change', mh)
      renderer.dispose()
      if (el.contains(renderer.domElement)) el.removeChild(renderer.domElement)
    }
  }, [service])

  return (
    <div
      ref={mountRef}
      aria-hidden="true"
      style={{ width: '100%', height: '100%' }}
    />
  )
}

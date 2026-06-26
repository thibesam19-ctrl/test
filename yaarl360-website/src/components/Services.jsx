import { useState } from 'react'
import ServiceCard3D from './ServiceCard3D'

const SERVICES = [
  {
    id: 'plumbing',
    title: 'Plumbing',
    desc: 'Leaks, blockages and emergency burst-pipe response by certified plumbers.',
    color: '#3B82F6',
    bg: '#EFF6FF',
  },
  {
    id: 'electrical',
    title: 'Electrical',
    desc: 'Wiring faults, breaker trips and certified electrical installations.',
    color: '#EAB308',
    bg: '#FEFCE8',
  },
  {
    id: 'cleaning',
    title: 'Deep Cleaning',
    desc: 'Full property, kitchen or bathroom deep cleans by trained professionals.',
    color: '#22C55E',
    bg: '#F0FDF4',
  },
  {
    id: 'ac',
    title: 'AC Service',
    desc: 'AC servicing, gas top-ups and breakdown repair — same day.',
    color: '#06B6D4',
    bg: '#ECFEFF',
  },
  {
    id: 'painting',
    title: 'Painting',
    desc: 'Interior and exterior painting with quality finishes and clean work.',
    color: '#A855F7',
    bg: '#FAF5FF',
  },
  {
    id: 'handyman',
    title: 'Handyman',
    desc: 'Furniture assembly, wall mounting and general home fixes.',
    color: '#F59E0B',
    bg: '#FFFBEB',
  },
  {
    id: 'garden',
    title: 'Garden Care',
    desc: 'Lawn trimming, hedge shaping and seasonal maintenance.',
    color: '#16A34A',
    bg: '#F0FDF4',
  },
  {
    id: 'pest',
    title: 'Pest Control',
    desc: 'Safe certified treatment for termites, rodents and insects.',
    color: '#EF4444',
    bg: '#FEF2F2',
  },
]

export default function Services() {
  const [hovered, setHovered] = useState(null)

  return (
    <section id="services" style={{ padding: '96px 24px', background: 'white' }}>
      <div style={{ maxWidth: 1200, margin: '0 auto' }}>
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: 64 }}>
          <span style={{
            display: 'inline-block',
            background: 'var(--color-muted)',
            color: 'var(--color-primary)',
            fontWeight: 600,
            fontSize: 13,
            padding: '6px 16px',
            borderRadius: 100,
            marginBottom: 16,
            letterSpacing: '0.06em',
            textTransform: 'uppercase',
          }}>
            Our Services
          </span>
          <h2 style={{
            fontSize: 'clamp(28px, 4vw, 44px)',
            fontWeight: 700,
            color: 'var(--color-primary-dark)',
            marginBottom: 16,
          }}>
            Everything Your Home Needs
          </h2>
          <p style={{
            fontSize: 17,
            color: 'var(--color-gray-600)',
            maxWidth: 520,
            margin: '0 auto',
            lineHeight: 1.7,
          }}>
            From emergency repairs to routine maintenance — all in one app, delivered by verified professionals.
          </p>
        </div>

        {/* Grid */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
          gap: 24,
        }}>
          {SERVICES.map((svc) => (
            <article
              key={svc.id}
              onMouseEnter={() => setHovered(svc.id)}
              onMouseLeave={() => setHovered(null)}
              style={{
                background: hovered === svc.id ? svc.bg : 'white',
                border: `2px solid ${hovered === svc.id ? svc.color : 'var(--color-border)'}`,
                borderRadius: 20,
                overflow: 'hidden',
                cursor: 'pointer',
                transition: 'all 220ms ease',
                transform: hovered === svc.id ? 'translateY(-6px)' : 'none',
                boxShadow: hovered === svc.id ? `0 12px 40px ${svc.color}25` : 'var(--shadow-card)',
              }}
            >
              {/* 3D canvas */}
              <div style={{
                height: 160,
                background: hovered === svc.id ? svc.bg : '#F8FAFC',
                transition: 'background 220ms ease',
              }}>
                <ServiceCard3D service={svc.id} />
              </div>

              {/* Text */}
              <div style={{ padding: '20px 24px 24px' }}>
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: 10,
                  marginBottom: 10,
                }}>
                  <div style={{
                    width: 10,
                    height: 10,
                    borderRadius: '50%',
                    background: svc.color,
                  }} />
                  <h3 style={{
                    fontSize: 17,
                    fontWeight: 600,
                    color: 'var(--color-primary-dark)',
                  }}>
                    {svc.title}
                  </h3>
                </div>
                <p style={{
                  fontSize: 14,
                  color: 'var(--color-gray-600)',
                  lineHeight: 1.6,
                }}>
                  {svc.desc}
                </p>
              </div>
            </article>
          ))}
        </div>
      </div>
    </section>
  )
}

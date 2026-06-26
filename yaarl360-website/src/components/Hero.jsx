import { useState } from 'react'
import { ChevronDown } from 'lucide-react'
import HeroScene from './HeroScene'

export default function Hero() {
  const [email, setEmail] = useState('')

  const scrollDown = () => {
    document.getElementById('services')?.scrollIntoView({ behavior: 'smooth' })
  }

  return (
    <section
      style={{
        position: 'relative',
        minHeight: '100dvh',
        background: 'linear-gradient(135deg, #1e3a8a 0%, #1E40AF 50%, #1d4ed8 100%)',
        display: 'flex',
        alignItems: 'center',
        overflow: 'hidden',
      }}
    >
      {/* 3D canvas */}
      <HeroScene />

      {/* Geometric background shapes */}
      <div aria-hidden="true" style={{ position: 'absolute', inset: 0, pointerEvents: 'none' }}>
        <div style={{
          position: 'absolute', top: '10%', right: '5%',
          width: 300, height: 300,
          background: 'rgba(59,130,246,0.15)',
          borderRadius: '50%',
          filter: 'blur(60px)',
        }} />
        <div style={{
          position: 'absolute', bottom: '15%', left: '3%',
          width: 200, height: 200,
          background: 'rgba(234,88,12,0.1)',
          borderRadius: '50%',
          filter: 'blur(40px)',
        }} />
      </div>

      {/* Content */}
      <div
        style={{
          position: 'relative',
          zIndex: 10,
          maxWidth: 1200,
          margin: '0 auto',
          padding: '120px 24px 80px',
          display: 'grid',
          gridTemplateColumns: '1fr 1fr',
          gap: 48,
          alignItems: 'center',
          width: '100%',
        }}
        className="hero-grid"
      >
        {/* Left — text */}
        <div>
          <div style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: 8,
            background: 'rgba(255,255,255,0.12)',
            border: '1px solid rgba(255,255,255,0.2)',
            borderRadius: 100,
            padding: '6px 16px',
            marginBottom: 24,
          }}>
            <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#4ADE80', display: 'inline-block', boxShadow: '0 0 8px #4ADE80' }} />
            <span style={{ color: 'rgba(255,255,255,0.9)', fontSize: 13, fontWeight: 500 }}>
              Now available in Sri Lanka
            </span>
          </div>

          <h1 style={{
            fontSize: 'clamp(36px, 5vw, 64px)',
            fontWeight: 800,
            color: 'white',
            marginBottom: 20,
            letterSpacing: '-0.02em',
          }}>
            Your Home,<br />
            <span style={{ color: 'var(--color-accent)' }}>Perfectly Serviced.</span>
          </h1>

          <p style={{
            fontSize: 18,
            color: 'rgba(255,255,255,0.8)',
            maxWidth: 480,
            lineHeight: 1.7,
            marginBottom: 40,
          }}>
            Book verified home service professionals in under 5 minutes. Plumbers, electricians, cleaners and more — on-demand or scheduled, right at your doorstep.
          </p>

          {/* Stats row */}
          <div style={{ display: 'flex', gap: 32, marginBottom: 40, flexWrap: 'wrap' }}>
            {[
              { n: '500+', label: 'Verified Workers' },
              { n: '8', label: 'Service Types' },
              { n: '< 5 min', label: 'Booking Time' },
            ].map(s => (
              <div key={s.label}>
                <div style={{ fontSize: 28, fontWeight: 700, color: 'white', fontFamily: 'var(--font-heading)' }}>{s.n}</div>
                <div style={{ fontSize: 13, color: 'rgba(255,255,255,0.65)' }}>{s.label}</div>
              </div>
            ))}
          </div>

          {/* Inline waitlist */}
          <form
            onSubmit={e => { e.preventDefault(); document.getElementById('waitlist')?.scrollIntoView({ behavior: 'smooth' }) }}
            style={{ display: 'flex', gap: 10, maxWidth: 440, flexWrap: 'wrap' }}
          >
            <input
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              aria-label="Email address"
              style={{
                flex: 1,
                minWidth: 200,
                padding: '14px 18px',
                borderRadius: 10,
                border: '1px solid rgba(255,255,255,0.3)',
                background: 'rgba(255,255,255,0.12)',
                color: 'white',
                fontSize: 15,
                backdropFilter: 'blur(8px)',
              }}
            />
            <button
              type="submit"
              style={{
                background: 'var(--color-accent)',
                color: 'white',
                fontWeight: 600,
                fontSize: 15,
                padding: '14px 24px',
                borderRadius: 10,
                transition: 'all 150ms ease',
                whiteSpace: 'nowrap',
              }}
              onMouseEnter={e => { e.currentTarget.style.transform = 'scale(0.97)' }}
              onMouseLeave={e => { e.currentTarget.style.transform = 'scale(1)' }}
            >
              Get Early Access
            </button>
          </form>
        </div>

        {/* Right — 3D canvas placeholder (HeroScene fills absolute) */}
        <div style={{ height: 440, position: 'relative' }} aria-hidden="true" />
      </div>

      {/* Scroll cue */}
      <button
        onClick={scrollDown}
        aria-label="Scroll to services"
        style={{
          position: 'absolute',
          bottom: 32,
          left: '50%',
          transform: 'translateX(-50%)',
          background: 'transparent',
          color: 'rgba(255,255,255,0.6)',
          animation: 'bounce 2s infinite',
          zIndex: 10,
        }}
      >
        <ChevronDown size={32} />
      </button>

      <style>{`
        @keyframes bounce {
          0%, 100% { transform: translateX(-50%) translateY(0); }
          50% { transform: translateX(-50%) translateY(8px); }
        }
        @media (max-width: 768px) {
          .hero-grid {
            grid-template-columns: 1fr !important;
          }
        }
      `}</style>
    </section>
  )
}

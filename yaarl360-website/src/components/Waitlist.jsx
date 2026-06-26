import { useState } from 'react'

export default function Waitlist() {
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [submitted, setSubmitted] = useState(false)
  const [loading, setLoading] = useState(false)

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!email) return
    setLoading(true)
    setTimeout(() => {
      setLoading(false)
      setSubmitted(true)
    }, 1200)
  }

  return (
    <section
      id="waitlist"
      style={{
        padding: '96px 24px',
        background: 'linear-gradient(135deg, #1e3a8a 0%, #1E40AF 100%)',
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      {/* BG decoration */}
      <div aria-hidden="true" style={{ position: 'absolute', inset: 0, pointerEvents: 'none' }}>
        <div style={{
          position: 'absolute', top: '-20%', right: '-10%',
          width: 500, height: 500,
          background: 'rgba(59,130,246,0.15)',
          borderRadius: '50%',
          filter: 'blur(80px)',
        }} />
        <div style={{
          position: 'absolute', bottom: '-15%', left: '-8%',
          width: 400, height: 400,
          background: 'rgba(234,88,12,0.1)',
          borderRadius: '50%',
          filter: 'blur(60px)',
        }} />
      </div>

      <div style={{
        maxWidth: 640,
        margin: '0 auto',
        position: 'relative',
        zIndex: 1,
        textAlign: 'center',
      }}>
        {!submitted ? (
          <>
            <span style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: 8,
              background: 'rgba(255,255,255,0.12)',
              border: '1px solid rgba(255,255,255,0.2)',
              borderRadius: 100,
              padding: '6px 18px',
              marginBottom: 24,
            }}>
              <span style={{
                width: 8, height: 8,
                borderRadius: '50%',
                background: '#FDE047',
                display: 'inline-block',
              }} />
              <span style={{ color: 'rgba(255,255,255,0.9)', fontSize: 13, fontWeight: 500 }}>
                Launching Soon in Sri Lanka
              </span>
            </span>

            <h2 style={{
              fontSize: 'clamp(28px, 4vw, 46px)',
              fontWeight: 800,
              color: 'white',
              marginBottom: 16,
              lineHeight: 1.2,
            }}>
              Be the First to Experience<br />
              <span style={{ color: '#FCA5A5' }}>YAARL 360</span>
            </h2>

            <p style={{
              fontSize: 17,
              color: 'rgba(255,255,255,0.75)',
              marginBottom: 48,
              lineHeight: 1.7,
            }}>
              Join the waitlist and get <strong style={{ color: 'white' }}>50% off your first booking</strong> when we launch. Be part of Sri Lanka's home services revolution.
            </p>

            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
              <input
                type="email"
                required
                placeholder="Your email address"
                value={email}
                onChange={e => setEmail(e.target.value)}
                aria-label="Email address"
                style={{
                  padding: '16px 20px',
                  borderRadius: 12,
                  border: '1px solid rgba(255,255,255,0.25)',
                  background: 'rgba(255,255,255,0.1)',
                  color: 'white',
                  fontSize: 16,
                  backdropFilter: 'blur(8px)',
                  width: '100%',
                }}
              />
              <input
                type="tel"
                placeholder="Phone number (optional)"
                value={phone}
                onChange={e => setPhone(e.target.value)}
                aria-label="Phone number"
                style={{
                  padding: '16px 20px',
                  borderRadius: 12,
                  border: '1px solid rgba(255,255,255,0.25)',
                  background: 'rgba(255,255,255,0.1)',
                  color: 'white',
                  fontSize: 16,
                  backdropFilter: 'blur(8px)',
                  width: '100%',
                }}
              />
              <button
                type="submit"
                disabled={loading}
                style={{
                  background: loading ? 'rgba(234,88,12,0.6)' : 'var(--color-accent)',
                  color: 'white',
                  fontWeight: 700,
                  fontSize: 16,
                  padding: '16px 32px',
                  borderRadius: 12,
                  transition: 'all 150ms ease',
                  opacity: loading ? 0.8 : 1,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: 10,
                  cursor: loading ? 'not-allowed' : 'pointer',
                  width: '100%',
                }}
                onMouseEnter={e => { if (!loading) e.currentTarget.style.transform = 'translateY(-2px)' }}
                onMouseLeave={e => { e.currentTarget.style.transform = 'none' }}
              >
                {loading ? (
                  <>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" style={{ animation: 'spin 0.8s linear infinite' }}>
                      <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" />
                    </svg>
                    Joining…
                  </>
                ) : (
                  <>
                    Join the Waitlist — Get 50% Off
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5" strokeLinecap="round"><path d="M5 12h14M13 6l6 6-6 6" /></svg>
                  </>
                )}
              </button>
            </form>

            <p style={{
              fontSize: 13,
              color: 'rgba(255,255,255,0.5)',
              marginTop: 20,
            }}>
              No spam. We'll only contact you about your early access.
            </p>
          </>
        ) : (
          <div>
            <div style={{
              width: 72,
              height: 72,
              background: 'rgba(74,222,128,0.2)',
              borderRadius: '50%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              margin: '0 auto 24px',
            }}>
              <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#4ADE80" strokeWidth="2.5" strokeLinecap="round"><path d="M5 13l4 4L19 7" /></svg>
            </div>
            <h3 style={{ fontSize: 32, fontWeight: 700, color: 'white', marginBottom: 12 }}>
              You're on the list! 🎉
            </h3>
            <p style={{ fontSize: 17, color: 'rgba(255,255,255,0.75)', lineHeight: 1.7 }}>
              Thanks for joining! We'll notify you at <strong style={{ color: 'white' }}>{email}</strong> the moment YAARL 360 launches in your area. Your 50% discount is reserved.
            </p>
          </div>
        )}
      </div>

      <style>{`
        @keyframes spin { to { transform: rotate(360deg); } }
        input::placeholder { color: rgba(255,255,255,0.5); }
      `}</style>
    </section>
  )
}

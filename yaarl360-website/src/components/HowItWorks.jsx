const STEPS = [
  {
    n: '01',
    title: 'Choose a Service',
    desc: 'Browse 8 home service categories and select what you need — from emergency plumbing to routine garden care.',
    icon: (
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="3" y="3" width="7" height="7" rx="1" />
        <rect x="14" y="3" width="7" height="7" rx="1" />
        <rect x="3" y="14" width="7" height="7" rx="1" />
        <rect x="14" y="14" width="7" height="7" rx="1" />
      </svg>
    ),
  },
  {
    n: '02',
    title: 'Book Instantly',
    desc: 'Pick a time that works — on-demand within the hour, or schedule in advance. Add your location and job notes.',
    icon: (
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="3" y="4" width="18" height="18" rx="2" />
        <line x1="16" y1="2" x2="16" y2="6" />
        <line x1="8" y1="2" x2="8" y2="6" />
        <line x1="3" y1="10" x2="21" y2="10" />
        <line x1="8" y1="14" x2="8" y2="14" strokeWidth="3" strokeLinecap="round" />
        <line x1="12" y1="14" x2="12" y2="14" strokeWidth="3" strokeLinecap="round" />
        <line x1="16" y1="14" x2="16" y2="14" strokeWidth="3" strokeLinecap="round" />
      </svg>
    ),
  },
  {
    n: '03',
    title: 'Track in Real-Time',
    desc: 'Watch your verified professional on the map as they head to you. Get push alerts at every stage.',
    icon: (
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="10" r="3" />
        <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z" />
      </svg>
    ),
  },
  {
    n: '04',
    title: 'Pay & Review',
    desc: 'Pay securely in-app. Get your digital invoice instantly and rate your professional to help the community.',
    icon: (
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="2" y="5" width="20" height="14" rx="2" />
        <line x1="2" y1="10" x2="22" y2="10" />
        <line x1="6" y1="15" x2="10" y2="15" />
      </svg>
    ),
  },
]

export default function HowItWorks() {
  return (
    <section
      id="how-it-works"
      style={{
        padding: '96px 24px',
        background: 'var(--color-background)',
      }}
    >
      <div style={{ maxWidth: 1200, margin: '0 auto' }}>
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: 64 }}>
          <span style={{
            display: 'inline-block',
            background: 'var(--color-primary)',
            color: 'white',
            fontWeight: 600,
            fontSize: 13,
            padding: '6px 16px',
            borderRadius: 100,
            marginBottom: 16,
            letterSpacing: '0.06em',
            textTransform: 'uppercase',
          }}>
            How It Works
          </span>
          <h2 style={{
            fontSize: 'clamp(28px, 4vw, 44px)',
            fontWeight: 700,
            color: 'var(--color-primary-dark)',
            marginBottom: 16,
          }}>
            Booked in Under 5 Minutes
          </h2>
          <p style={{
            fontSize: 17,
            color: 'var(--color-gray-600)',
            maxWidth: 520,
            margin: '0 auto',
            lineHeight: 1.7,
          }}>
            Four simple steps from need to done — no phone calls, no waiting.
          </p>
        </div>

        {/* Steps */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
          gap: 32,
          position: 'relative',
        }}>
          {STEPS.map((step, i) => (
            <div
              key={step.n}
              style={{
                position: 'relative',
                background: 'white',
                borderRadius: 20,
                padding: '36px 28px',
                boxShadow: 'var(--shadow-card)',
                border: '1px solid var(--color-border)',
              }}
            >
              {/* Step number */}
              <div style={{
                position: 'absolute',
                top: -18,
                left: 28,
                background: 'var(--color-primary)',
                color: 'white',
                fontFamily: 'var(--font-heading)',
                fontWeight: 700,
                fontSize: 13,
                padding: '6px 14px',
                borderRadius: 100,
                letterSpacing: '0.04em',
              }}>
                {step.n}
              </div>

              {/* Icon */}
              <div style={{
                width: 56,
                height: 56,
                background: 'var(--color-background)',
                borderRadius: 14,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: 'var(--color-primary)',
                marginBottom: 20,
              }}>
                {step.icon}
              </div>

              <h3 style={{
                fontSize: 18,
                fontWeight: 600,
                color: 'var(--color-primary-dark)',
                marginBottom: 10,
              }}>
                {step.title}
              </h3>
              <p style={{
                fontSize: 14,
                color: 'var(--color-gray-600)',
                lineHeight: 1.65,
              }}>
                {step.desc}
              </p>

              {/* Connector arrow (not last) */}
              {i < STEPS.length - 1 && (
                <div
                  aria-hidden="true"
                  style={{
                    position: 'absolute',
                    right: -20,
                    top: '50%',
                    transform: 'translateY(-50%)',
                    zIndex: 2,
                    color: 'var(--color-secondary)',
                    display: 'flex',
                    alignItems: 'center',
                  }}
                  className="step-arrow"
                >
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
                    <path d="M5 12h14M13 6l6 6-6 6" />
                  </svg>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>

      <style>{`
        @media (max-width: 768px) {
          .step-arrow { display: none !important; }
        }
      `}</style>
    </section>
  )
}

import { Check } from 'lucide-react'

const PLANS = [
  {
    name: 'Basic',
    price: 'Rs. 3,500',
    period: '/month',
    tagline: 'Essential home care',
    color: 'var(--color-secondary)',
    highlighted: false,
    features: [
      '1 deep clean per month',
      '2 handyman checkups',
      '12-month service calendar',
      'Auto-renew subscription',
      'Priority booking',
      'Digital invoices',
    ],
  },
  {
    name: 'Premium',
    price: 'Rs. 8,500',
    period: '/month',
    tagline: 'Complete villa care',
    color: 'var(--color-primary)',
    highlighted: true,
    badge: 'Most Popular',
    features: [
      'Full property clean every 6 weeks',
      'Unlimited 24/7 priority checkups',
      'Visitation fees waived',
      'Top-of-queue dispatch',
      'Dedicated account manager',
      'Annual home health report',
    ],
  },
]

export default function Pricing() {
  return (
    <section
      id="pricing"
      style={{
        padding: '96px 24px',
        background: 'white',
      }}
    >
      <div style={{ maxWidth: 900, margin: '0 auto' }}>
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
            Subscription Plans
          </span>
          <h2 style={{
            fontSize: 'clamp(28px, 4vw, 44px)',
            fontWeight: 700,
            color: 'var(--color-primary-dark)',
            marginBottom: 16,
          }}>
            Simple, Transparent Pricing
          </h2>
          <p style={{
            fontSize: 17,
            color: 'var(--color-gray-600)',
            maxWidth: 500,
            margin: '0 auto',
            lineHeight: 1.7,
          }}>
            Subscribe once and never worry about home maintenance again.
          </p>
        </div>

        {/* Cards */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
          gap: 28,
          alignItems: 'start',
        }}>
          {PLANS.map((plan) => (
            <div
              key={plan.name}
              style={{
                position: 'relative',
                background: plan.highlighted ? 'var(--color-primary)' : 'white',
                border: `2px solid ${plan.highlighted ? 'var(--color-primary)' : 'var(--color-border)'}`,
                borderRadius: 24,
                padding: '40px 32px',
                boxShadow: plan.highlighted ? '0 20px 60px rgba(30,64,175,0.25)' : 'var(--shadow-card)',
                transform: plan.highlighted ? 'scale(1.03)' : 'none',
              }}
            >
              {/* Badge */}
              {plan.badge && (
                <div style={{
                  position: 'absolute',
                  top: -16,
                  left: '50%',
                  transform: 'translateX(-50%)',
                  background: 'var(--color-accent)',
                  color: 'white',
                  fontWeight: 700,
                  fontSize: 12,
                  padding: '6px 18px',
                  borderRadius: 100,
                  whiteSpace: 'nowrap',
                  letterSpacing: '0.04em',
                }}>
                  {plan.badge}
                </div>
              )}

              <div style={{
                fontSize: 13,
                fontWeight: 600,
                color: plan.highlighted ? 'rgba(255,255,255,0.7)' : 'var(--color-gray-600)',
                letterSpacing: '0.06em',
                textTransform: 'uppercase',
                marginBottom: 8,
              }}>
                {plan.name}
              </div>

              <div style={{ marginBottom: 4 }}>
                <span style={{
                  fontFamily: 'var(--font-heading)',
                  fontSize: 40,
                  fontWeight: 800,
                  color: plan.highlighted ? 'white' : 'var(--color-primary-dark)',
                }}>
                  {plan.price}
                </span>
                <span style={{
                  fontSize: 15,
                  color: plan.highlighted ? 'rgba(255,255,255,0.7)' : 'var(--color-gray-600)',
                }}>
                  {plan.period}
                </span>
              </div>

              <p style={{
                fontSize: 14,
                color: plan.highlighted ? 'rgba(255,255,255,0.75)' : 'var(--color-gray-600)',
                marginBottom: 32,
              }}>
                {plan.tagline}
              </p>

              {/* Features */}
              <ul style={{ listStyle: 'none', marginBottom: 36, display: 'flex', flexDirection: 'column', gap: 14 }}>
                {plan.features.map((f) => (
                  <li key={f} style={{ display: 'flex', alignItems: 'flex-start', gap: 12 }}>
                    <div style={{
                      width: 22,
                      height: 22,
                      borderRadius: '50%',
                      background: plan.highlighted ? 'rgba(255,255,255,0.2)' : 'var(--color-background)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      flexShrink: 0,
                      marginTop: 1,
                    }}>
                      <Check size={13} color={plan.highlighted ? 'white' : 'var(--color-success)'} strokeWidth={3} />
                    </div>
                    <span style={{
                      fontSize: 14,
                      color: plan.highlighted ? 'rgba(255,255,255,0.9)' : 'var(--color-gray-800)',
                      lineHeight: 1.5,
                    }}>
                      {f}
                    </span>
                  </li>
                ))}
              </ul>

              <a
                href="#waitlist"
                style={{
                  display: 'block',
                  textAlign: 'center',
                  background: plan.highlighted ? 'white' : 'var(--color-primary)',
                  color: plan.highlighted ? 'var(--color-primary)' : 'white',
                  fontWeight: 700,
                  fontSize: 15,
                  padding: '14px 24px',
                  borderRadius: 12,
                  transition: 'all 150ms ease',
                }}
                onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-2px)' }}
                onMouseLeave={e => { e.currentTarget.style.transform = 'none' }}
              >
                Get Started — {plan.name}
              </a>
            </div>
          ))}
        </div>

        {/* Note */}
        <p style={{
          textAlign: 'center',
          fontSize: 14,
          color: 'var(--color-gray-600)',
          marginTop: 40,
        }}>
          All plans include in-app payments, digital invoices and 24/7 customer support. Cancel anytime.
        </p>
      </div>

      <style>{`
        @media (max-width: 640px) {
          #pricing > div > div:last-of-type > div:nth-child(2) {
            transform: none !important;
          }
        }
      `}</style>
    </section>
  )
}

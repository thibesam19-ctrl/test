"use client";

import { useState } from "react";
import Link from "next/link";
import { Check, Zap } from "lucide-react";

const PLANS = [
  {
    name: "Basic",
    price: { monthly: 3500, annual: 2900 },
    desc: "Perfect for apartments and small homes",
    features: [
      "Monthly deep clean — 1 bathroom & kitchen",
      "2 complimentary handyman checkups/month",
      "Priority booking (24hr ahead)",
      "In-app scheduling calendar",
      "Email & chat support",
    ],
    cta: "Get Basic",
    highlight: false,
  },
  {
    name: "Premium",
    price: { monthly: 8500, annual: 7000 },
    desc: "Full home coverage with priority response",
    features: [
      "Full property deep clean every 6 weeks",
      "Priority 24/7 on-demand emergency response",
      "Waived visitation fees on all callouts",
      "Dedicated account manager",
      "Year-round auto-scheduled calendar",
      "Failed payment retry & grace period",
    ],
    cta: "Get Premium",
    highlight: true,
    badge: "Most Popular",
  },
  {
    name: "Villa / Corporate",
    price: { monthly: 22000, annual: 18000 },
    desc: "Multi-property and enterprise contracts",
    features: [
      "Everything in Premium",
      "Up to 3 properties covered",
      "Dedicated in-house crew assignment",
      "Monthly invoice & corporate billing",
      "Custom service scheduling",
      "SLA-guaranteed response times",
    ],
    cta: "Contact Sales",
    highlight: false,
  },
];

export default function SubscriptionPlans() {
  const [billing, setBilling] = useState<"monthly" | "annual">("monthly");

  return (
    <section id="pricing" className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <p className="text-orange-500 font-semibold text-sm uppercase tracking-widest mb-3">Subscription Plans</p>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-gray-900 mb-4">
            Maintenance made simple
          </h2>
          <p className="text-gray-500 max-w-xl mx-auto mb-8">
            Subscribe and never worry about scheduling home maintenance again. Auto-renewing plans with full calendar control.
          </p>

          <div className="inline-flex items-center bg-gray-100 rounded-full p-1 gap-1">
            <button
              onClick={() => setBilling("monthly")}
              className={`px-5 py-2 rounded-full text-sm font-semibold transition-all ${billing === "monthly" ? "bg-white text-gray-900 shadow-sm" : "text-gray-500"}`}
            >
              Monthly
            </button>
            <button
              onClick={() => setBilling("annual")}
              className={`px-5 py-2 rounded-full text-sm font-semibold transition-all ${billing === "annual" ? "bg-white text-gray-900 shadow-sm" : "text-gray-500"}`}
            >
              Annual <span className="text-green-600 font-bold">–17%</span>
            </button>
          </div>
        </div>

        <div className="grid md:grid-cols-3 gap-6 items-start">
          {PLANS.map((plan) => (
            <div
              key={plan.name}
              className={`rounded-2xl border p-7 flex flex-col gap-5 relative ${
                plan.highlight
                  ? "border-orange-400 shadow-xl shadow-orange-100 bg-orange-500 text-white"
                  : "border-gray-200 bg-white text-gray-900"
              }`}
            >
              {plan.badge && (
                <div className="absolute -top-3 left-1/2 -translate-x-1/2">
                  <span className="bg-gray-900 text-white text-xs font-bold px-3 py-1 rounded-full flex items-center gap-1">
                    <Zap className="w-3 h-3" /> {plan.badge}
                  </span>
                </div>
              )}

              <div>
                <h3 className={`font-bold text-lg mb-1 ${plan.highlight ? "text-white" : "text-gray-900"}`}>{plan.name}</h3>
                <p className={`text-sm ${plan.highlight ? "text-orange-100" : "text-gray-500"}`}>{plan.desc}</p>
              </div>

              <div>
                <span className={`text-4xl font-extrabold ${plan.highlight ? "text-white" : "text-gray-900"}`}>
                  LKR {plan.price[billing].toLocaleString()}
                </span>
                <span className={`text-sm ml-1 ${plan.highlight ? "text-orange-100" : "text-gray-500"}`}>/month</span>
              </div>

              <ul className="space-y-2.5">
                {plan.features.map((f) => (
                  <li key={f} className={`flex items-start gap-2 text-sm ${plan.highlight ? "text-orange-50" : "text-gray-600"}`}>
                    <Check className={`w-4 h-4 mt-0.5 flex-shrink-0 ${plan.highlight ? "text-white" : "text-orange-500"}`} />
                    {f}
                  </li>
                ))}
              </ul>

              <Link
                href={plan.name === "Villa / Corporate" ? "#b2b" : "/book?tab=subscribe"}
                className={`text-center font-semibold py-3 rounded-full transition-colors ${
                  plan.highlight
                    ? "bg-white text-orange-500 hover:bg-orange-50"
                    : "bg-orange-500 hover:bg-orange-600 text-white"
                }`}
              >
                {plan.cta}
              </Link>
            </div>
          ))}
        </div>

        <p className="text-center text-xs text-gray-400 mt-8">
          All plans include auto-renew with secure card tokenization. Cancel or reschedule anytime — up to 24 hours before a visit.
        </p>
      </div>
    </section>
  );
}

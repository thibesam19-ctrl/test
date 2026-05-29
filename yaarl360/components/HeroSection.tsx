"use client";

import { useState } from "react";
import Link from "next/link";
import { Search, MapPin, Star, Shield, Clock } from "lucide-react";

const CITIES = ["Colombo", "Jaffna", "Kandy", "Galle", "Negombo", "Wellawatte", "Batticaloa", "Trincomalee", "Matara", "Kurunegala"];

export default function HeroSection() {
  const [city, setCity] = useState("");
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [checked, setChecked] = useState<string | null>(null);

  function handleInput(val: string) {
    setCity(val);
    setChecked(null);
    if (val.length > 1) {
      setSuggestions(CITIES.filter((c) => c.toLowerCase().includes(val.toLowerCase())));
    } else {
      setSuggestions([]);
    }
  }

  function handleCheck() {
    const match = CITIES.find((c) => c.toLowerCase() === city.toLowerCase());
    setChecked(match ? "available" : "unavailable");
    setSuggestions([]);
  }

  return (
    <section className="pt-24 pb-16 bg-gradient-to-br from-orange-50 via-white to-amber-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Left */}
          <div>
            <div className="inline-flex items-center gap-2 bg-orange-100 text-orange-700 text-xs font-semibold px-3 py-1.5 rounded-full mb-6">
              <span className="w-2 h-2 bg-orange-500 rounded-full animate-pulse" />
              Live in Colombo, Jaffna & Kandy
            </div>

            <h1 className="text-4xl sm:text-5xl font-extrabold text-gray-900 leading-tight mb-6">
              Home Services,<br />
              <span className="text-orange-500">On Demand.</span>
            </h1>

            <p className="text-lg text-gray-600 mb-8 leading-relaxed">
              Instant plumbing, electrical, deep cleaning, AC repair and more — matched to a verified professional near you in minutes. Covering Sri Lanka&apos;s homes and businesses.
            </p>

            {/* City search */}
            <div className="bg-white rounded-2xl shadow-md border border-gray-100 p-4 mb-6 relative">
              <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">Check your area</p>
              <div className="flex gap-2">
                <div className="relative flex-1">
                  <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                  <input
                    type="text"
                    placeholder="Enter your city (e.g. Jaffna)"
                    value={city}
                    onChange={(e) => handleInput(e.target.value)}
                    onKeyDown={(e) => e.key === "Enter" && handleCheck()}
                    className="w-full pl-10 pr-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-orange-300"
                  />
                  {suggestions.length > 0 && (
                    <div className="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-10 overflow-hidden">
                      {suggestions.map((s) => (
                        <button
                          key={s}
                          className="w-full text-left px-4 py-2.5 text-sm hover:bg-orange-50 hover:text-orange-600 transition-colors"
                          onClick={() => { setCity(s); setSuggestions([]); setChecked(null); }}
                        >
                          {s}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
                <button
                  onClick={handleCheck}
                  className="bg-orange-500 hover:bg-orange-600 text-white px-4 py-2.5 rounded-xl flex items-center gap-1.5 text-sm font-medium transition-colors"
                >
                  <Search className="w-4 h-4" />
                  Check
                </button>
              </div>

              {checked === "available" && (
                <div className="mt-3 flex items-center gap-2 text-green-600 text-sm font-medium">
                  <span className="w-2 h-2 bg-green-500 rounded-full" />
                  Great! Services are available in {city}.
                  <Link href="/book" className="underline text-orange-500">Book now →</Link>
                </div>
              )}
              {checked === "unavailable" && (
                <div className="mt-3 flex items-center gap-2 text-gray-500 text-sm">
                  <span className="w-2 h-2 bg-gray-400 rounded-full" />
                  Not in {city} yet. We&apos;re expanding soon!
                </div>
              )}
            </div>

            <div className="flex flex-wrap gap-3">
              <Link
                href="/book"
                className="bg-orange-500 hover:bg-orange-600 text-white font-semibold px-6 py-3 rounded-full transition-colors shadow-md shadow-orange-200"
              >
                Book a Service
              </Link>
              <Link
                href="#pricing"
                className="border-2 border-orange-500 text-orange-500 hover:bg-orange-50 font-semibold px-6 py-3 rounded-full transition-colors"
              >
                View Plans
              </Link>
            </div>
          </div>

          {/* Right — Stats */}
          <div className="grid grid-cols-2 gap-4">
            {[
              { icon: <Star className="w-6 h-6 text-yellow-500" />, value: "4.9★", label: "Average Rating", bg: "bg-yellow-50" },
              { icon: <Shield className="w-6 h-6 text-green-500" />, value: "100%", label: "Verified Workers", bg: "bg-green-50" },
              { icon: <Clock className="w-6 h-6 text-orange-500" />, value: "<15 min", label: "Response Time", bg: "bg-orange-50" },
              { icon: <MapPin className="w-6 h-6 text-blue-500" />, value: "10+", label: "Cities Covered", bg: "bg-blue-50" },
            ].map((s) => (
              <div key={s.label} className={`${s.bg} rounded-2xl p-6 flex flex-col gap-3`}>
                <div className="w-12 h-12 bg-white rounded-xl flex items-center justify-center shadow-sm">
                  {s.icon}
                </div>
                <div className="text-2xl font-extrabold text-gray-900">{s.value}</div>
                <div className="text-sm text-gray-600">{s.label}</div>
              </div>
            ))}

            <div className="col-span-2 bg-gray-900 rounded-2xl p-6 text-white">
              <p className="text-xs font-semibold text-orange-400 uppercase tracking-wide mb-2">Emergency Dispatch</p>
              <p className="text-sm font-medium leading-relaxed">
                Pipe burst? AC failed? Our gig workers are on standby 24/7 — broadcast to the nearest technician within seconds.
              </p>
              <Link href="/book?type=emergency" className="mt-3 inline-flex items-center gap-1 text-orange-400 text-sm font-semibold hover:text-orange-300 transition-colors">
                Request now →
              </Link>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

"use client";

import Link from "next/link";
import { Zap, Droplets, Wind, Paintbrush, Sparkles, Wrench, HardHat, Bug } from "lucide-react";

const SERVICES = [
  { icon: <Droplets className="w-7 h-7" />, name: "Plumbing", desc: "Leaks, pipes, fixtures, drainage", color: "text-blue-500", bg: "bg-blue-50", badge: "Emergency" },
  { icon: <Zap className="w-7 h-7" />, name: "Electrical", desc: "Wiring, fuse boards, installations", color: "text-yellow-500", bg: "bg-yellow-50", badge: "Emergency" },
  { icon: <Wind className="w-7 h-7" />, name: "AC Repair", desc: "Servicing, gas refill, installation", color: "text-sky-500", bg: "bg-sky-50", badge: "Emergency" },
  { icon: <Sparkles className="w-7 h-7" />, name: "Deep Cleaning", desc: "Full home, kitchen & bathroom cleans", color: "text-green-500", bg: "bg-green-50", badge: "Scheduled" },
  { icon: <Paintbrush className="w-7 h-7" />, name: "Painting", desc: "Interior, exterior & feature walls", color: "text-purple-500", bg: "bg-purple-50", badge: "Scheduled" },
  { icon: <Wrench className="w-7 h-7" />, name: "Handyman", desc: "Furniture assembly, minor repairs", color: "text-orange-500", bg: "bg-orange-50", badge: "Both" },
  { icon: <HardHat className="w-7 h-7" />, name: "Renovation", desc: "Tiling, carpentry, structural work", color: "text-red-500", bg: "bg-red-50", badge: "Scheduled" },
  { icon: <Bug className="w-7 h-7" />, name: "Pest Control", desc: "Termites, rodents, insects", color: "text-teal-500", bg: "bg-teal-50", badge: "Scheduled" },
];

const BADGE_COLORS: Record<string, string> = {
  Emergency: "bg-red-100 text-red-600",
  Scheduled: "bg-blue-100 text-blue-600",
  Both: "bg-orange-100 text-orange-600",
};

export default function ServicesSection() {
  return (
    <section id="services" className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-14">
          <p className="text-orange-500 font-semibold text-sm uppercase tracking-widest mb-3">What We Offer</p>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-gray-900 mb-4">
            Every home service, one platform
          </h2>
          <p className="text-gray-500 max-w-xl mx-auto">
            From emergency repairs dispatched in minutes to scheduled deep cleans — all handled by verified, rated professionals.
          </p>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {SERVICES.map((s) => (
            <div
              key={s.name}
              className="group border border-gray-100 rounded-2xl p-5 hover:shadow-md hover:border-orange-200 transition-all cursor-pointer"
            >
              <div className={`${s.bg} ${s.color} w-12 h-12 rounded-xl flex items-center justify-center mb-4 group-hover:scale-105 transition-transform`}>
                {s.icon}
              </div>
              <div className="flex items-start justify-between gap-1 mb-1">
                <h3 className="font-bold text-gray-900 text-sm">{s.name}</h3>
                <span className={`text-[10px] font-semibold px-1.5 py-0.5 rounded-full ${BADGE_COLORS[s.badge]}`}>
                  {s.badge}
                </span>
              </div>
              <p className="text-xs text-gray-500 leading-relaxed">{s.desc}</p>
            </div>
          ))}
        </div>

        <div className="text-center mt-10">
          <Link
            href="/book"
            className="inline-block bg-orange-500 hover:bg-orange-600 text-white font-semibold px-8 py-3 rounded-full transition-colors shadow-md shadow-orange-200"
          >
            Book Any Service
          </Link>
        </div>
      </div>
    </section>
  );
}

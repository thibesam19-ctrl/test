"use client";

import { useState } from "react";
import Link from "next/link";
import { Menu, X, Wrench } from "lucide-react";

export default function Navbar() {
  const [open, setOpen] = useState(false);

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-white/95 backdrop-blur border-b border-gray-100 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link href="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-orange-500 rounded-lg flex items-center justify-center">
              <Wrench className="w-4 h-4 text-white" />
            </div>
            <span className="text-xl font-bold text-gray-900">
              YAARL <span className="text-orange-500">360</span>
            </span>
          </Link>

          <div className="hidden md:flex items-center gap-8">
            <a href="#services" className="text-sm text-gray-600 hover:text-orange-500 transition-colors">Services</a>
            <a href="#how-it-works" className="text-sm text-gray-600 hover:text-orange-500 transition-colors">How it Works</a>
            <a href="#pricing" className="text-sm text-gray-600 hover:text-orange-500 transition-colors">Pricing</a>
            <a href="#b2b" className="text-sm text-gray-600 hover:text-orange-500 transition-colors">For Business</a>
            <a href="#join" className="text-sm text-gray-600 hover:text-orange-500 transition-colors">Join as Worker</a>
          </div>

          <div className="hidden md:flex items-center gap-3">
            <Link href="/book" className="text-sm font-medium text-orange-500 hover:text-orange-600 transition-colors">
              Book Now
            </Link>
            <Link
              href="/book"
              className="bg-orange-500 hover:bg-orange-600 text-white text-sm font-medium px-4 py-2 rounded-full transition-colors"
            >
              Get Started
            </Link>
          </div>

          <button
            className="md:hidden p-2 rounded-lg hover:bg-gray-100"
            onClick={() => setOpen(!open)}
          >
            {open ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>
      </div>

      {open && (
        <div className="md:hidden border-t border-gray-100 bg-white px-4 py-4 space-y-3">
          {["Services", "How it Works", "Pricing", "For Business", "Join as Worker"].map((item) => (
            <a
              key={item}
              href={`#${item.toLowerCase().replace(/ /g, "-")}`}
              className="block text-sm text-gray-600 hover:text-orange-500 py-1"
              onClick={() => setOpen(false)}
            >
              {item}
            </a>
          ))}
          <Link
            href="/book"
            className="block w-full text-center bg-orange-500 hover:bg-orange-600 text-white text-sm font-medium px-4 py-2.5 rounded-full transition-colors mt-2"
          >
            Book Now
          </Link>
        </div>
      )}
    </nav>
  );
}

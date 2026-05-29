"use client";

import { useState } from "react";
import { Upload, CheckCircle, DollarSign, Clock, Star, Shield } from "lucide-react";

export default function WorkerSection() {
  const [submitted, setSubmitted] = useState(false);
  const [form, setForm] = useState({ name: "", phone: "", email: "", skill: "", city: "", id: "" });

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitted(true);
  }

  return (
    <section id="join" className="py-20 bg-orange-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid lg:grid-cols-2 gap-12 items-start">
          {/* Left */}
          <div>
            <p className="text-orange-500 font-semibold text-sm uppercase tracking-widest mb-3">For Workers</p>
            <h2 className="text-3xl sm:text-4xl font-extrabold text-gray-900 mb-5 leading-tight">
              Earn on your terms.<br />
              <span className="text-orange-500">Join YAARL 360.</span>
            </h2>
            <p className="text-gray-600 mb-8">
              Are you a plumber, electrician, AC technician, or cleaning professional? Join our verified marketplace and receive live job requests in your area — accept or reject, you&apos;re in control.
            </p>

            <div className="grid grid-cols-2 gap-4 mb-8">
              {[
                { icon: <DollarSign className="w-5 h-5 text-green-600" />, title: "80% Earnings", desc: "Keep 80% of every job. We charge 20% platform fee.", bg: "bg-green-50" },
                { icon: <Clock className="w-5 h-5 text-blue-600" />, title: "Flexible Hours", desc: "Go online only when you want to work.", bg: "bg-blue-50" },
                { icon: <Star className="w-5 h-5 text-yellow-600" />, title: "Build Reputation", desc: "Top-rated workers get priority dispatch.", bg: "bg-yellow-50" },
                { icon: <Shield className="w-5 h-5 text-purple-600" />, title: "Verified Badge", desc: "Get a YAARL Verified badge to build trust.", bg: "bg-purple-50" },
              ].map((b) => (
                <div key={b.title} className={`${b.bg} rounded-xl p-4 border border-white`}>
                  <div className="mb-2">{b.icon}</div>
                  <h4 className="font-bold text-gray-900 text-sm mb-1">{b.title}</h4>
                  <p className="text-xs text-gray-600">{b.desc}</p>
                </div>
              ))}
            </div>
          </div>

          {/* Right — Form */}
          <div className="bg-white rounded-2xl p-8 shadow-sm border border-gray-100">
            {submitted ? (
              <div className="flex flex-col items-center justify-center py-12 gap-4 text-center">
                <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
                  <CheckCircle className="w-8 h-8 text-green-500" />
                </div>
                <h3 className="text-xl font-bold text-gray-900">Application Submitted!</h3>
                <p className="text-gray-500 text-sm max-w-xs">Our team will review your application and contact you within 48 hours for verification.</p>
              </div>
            ) : (
              <>
                <h3 className="text-xl font-bold text-gray-900 mb-6">Apply to Join the Network</h3>
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="text-xs font-semibold text-gray-600 block mb-1">Full Name *</label>
                      <input required value={form.name} onChange={e => setForm({...form, name: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" placeholder="Kamal Perera" />
                    </div>
                    <div>
                      <label className="text-xs font-semibold text-gray-600 block mb-1">Phone *</label>
                      <input required type="tel" value={form.phone} onChange={e => setForm({...form, phone: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" placeholder="+94 77 000 0000" />
                    </div>
                  </div>
                  <div>
                    <label className="text-xs font-semibold text-gray-600 block mb-1">Email</label>
                    <input type="email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" placeholder="kamal@email.com" />
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="text-xs font-semibold text-gray-600 block mb-1">Primary Skill *</label>
                      <select required value={form.skill} onChange={e => setForm({...form, skill: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300 bg-white">
                        <option value="">Select skill</option>
                        <option>Plumber</option>
                        <option>Electrician</option>
                        <option>AC Technician</option>
                        <option>Cleaner</option>
                        <option>Painter</option>
                        <option>Handyman</option>
                        <option>Pest Control</option>
                      </select>
                    </div>
                    <div>
                      <label className="text-xs font-semibold text-gray-600 block mb-1">Your City *</label>
                      <select required value={form.city} onChange={e => setForm({...form, city: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300 bg-white">
                        <option value="">Select city</option>
                        {["Colombo", "Jaffna", "Kandy", "Galle", "Negombo", "Wellawatte", "Batticaloa"].map(c => <option key={c}>{c}</option>)}
                      </select>
                    </div>
                  </div>
                  <div>
                    <label className="text-xs font-semibold text-gray-600 block mb-1">National ID Number *</label>
                    <input required value={form.id} onChange={e => setForm({...form, id: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" placeholder="000000000V or 000000000000" />
                  </div>
                  <div className="border-2 border-dashed border-gray-200 rounded-xl p-4 flex flex-col items-center gap-2 text-center cursor-pointer hover:border-orange-300 transition-colors">
                    <Upload className="w-6 h-6 text-gray-400" />
                    <p className="text-xs text-gray-500">Upload certifications / NIC copy <span className="text-orange-500">(optional)</span></p>
                    <p className="text-xs text-gray-400">PNG, JPG, PDF — max 5MB</p>
                  </div>
                  <button type="submit" className="w-full bg-orange-500 hover:bg-orange-600 text-white font-semibold py-3 rounded-full transition-colors">
                    Submit Application
                  </button>
                </form>
              </>
            )}
          </div>
        </div>
      </div>
    </section>
  );
}

"use client";

import { useState } from "react";
import { Building2, HotelIcon, Briefcase, CheckCircle } from "lucide-react";

export default function B2BSection() {
  const [submitted, setSubmitted] = useState(false);
  const [form, setForm] = useState({ company: "", contact: "", email: "", phone: "", type: "", message: "" });

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitted(true);
  }

  return (
    <section id="b2b" className="py-20 bg-gray-900 text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid lg:grid-cols-2 gap-12 items-start">
          {/* Left */}
          <div>
            <p className="text-orange-400 font-semibold text-sm uppercase tracking-widest mb-3">For Business</p>
            <h2 className="text-3xl sm:text-4xl font-extrabold mb-5 leading-tight">
              Enterprise & B2B<br />
              <span className="text-orange-400">Corporate Plans</span>
            </h2>
            <p className="text-gray-400 mb-8 leading-relaxed">
              Hotels, offices, and corporate complexes across Sri Lanka trust YAARL 360 for consistent, high-quality facility maintenance. Custom contracts, monthly invoicing, and dedicated in-house crews.
            </p>

            <div className="space-y-4">
              {[
                { icon: <HotelIcon className="w-5 h-5 text-orange-400" />, title: "Luxury Hotels & Boutique Resorts", desc: "Recurring deep cleans, maintenance retainers, and 24/7 emergency response SLAs." },
                { icon: <Building2 className="w-5 h-5 text-orange-400" />, title: "Office Complexes", desc: "Weekly scheduled cleaning, electrical, plumbing maintenance contracts." },
                { icon: <Briefcase className="w-5 h-5 text-orange-400" />, title: "Corporate Chains", desc: "Multi-location contracts with centralized billing, reporting, and account management." },
              ].map((item) => (
                <div key={item.title} className="flex gap-4 bg-white/5 rounded-xl p-4 border border-white/10">
                  <div className="mt-0.5">{item.icon}</div>
                  <div>
                    <h4 className="font-semibold text-white text-sm mb-1">{item.title}</h4>
                    <p className="text-gray-400 text-sm">{item.desc}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Right — Form */}
          <div className="bg-white rounded-2xl p-8 text-gray-900">
            {submitted ? (
              <div className="flex flex-col items-center justify-center py-12 gap-4 text-center">
                <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
                  <CheckCircle className="w-8 h-8 text-green-500" />
                </div>
                <h3 className="text-xl font-bold text-gray-900">Request Received!</h3>
                <p className="text-gray-500 text-sm">Our B2B team will contact you within 24 hours to discuss your corporate plan.</p>
              </div>
            ) : (
              <>
                <h3 className="text-xl font-bold text-gray-900 mb-6">Request a Corporate Quote</h3>
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="text-xs font-semibold text-gray-600 block mb-1">Company Name *</label>
                      <input required value={form.company} onChange={e => setForm({...form, company: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" placeholder="ACME Hotels" />
                    </div>
                    <div>
                      <label className="text-xs font-semibold text-gray-600 block mb-1">Contact Person *</label>
                      <input required value={form.contact} onChange={e => setForm({...form, contact: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" placeholder="John Silva" />
                    </div>
                  </div>
                  <div>
                    <label className="text-xs font-semibold text-gray-600 block mb-1">Business Email *</label>
                    <input required type="email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" placeholder="john@acmehotels.lk" />
                  </div>
                  <div>
                    <label className="text-xs font-semibold text-gray-600 block mb-1">Phone *</label>
                    <input required type="tel" value={form.phone} onChange={e => setForm({...form, phone: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" placeholder="+94 77 000 0000" />
                  </div>
                  <div>
                    <label className="text-xs font-semibold text-gray-600 block mb-1">Business Type</label>
                    <select value={form.type} onChange={e => setForm({...form, type: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300 bg-white">
                      <option value="">Select type</option>
                      <option>Hotel / Resort</option>
                      <option>Office Complex</option>
                      <option>Corporate Chain</option>
                      <option>Apartment Complex</option>
                      <option>Other</option>
                    </select>
                  </div>
                  <div>
                    <label className="text-xs font-semibold text-gray-600 block mb-1">Requirements</label>
                    <textarea value={form.message} onChange={e => setForm({...form, message: e.target.value})} rows={3} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300 resize-none" placeholder="Describe your maintenance needs..." />
                  </div>
                  <button type="submit" className="w-full bg-orange-500 hover:bg-orange-600 text-white font-semibold py-3 rounded-full transition-colors">
                    Send Enquiry
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

"use client";

import { useState } from "react";
import Link from "next/link";
import { Zap, Calendar, ArrowLeft, CheckCircle, Clock, MapPin, Wrench } from "lucide-react";

const SERVICES = ["Plumbing", "Electrical", "AC Repair", "Deep Cleaning", "Painting", "Handyman", "Pest Control", "Renovation"];

type BookingType = "emergency" | "scheduled" | null;

export default function BookPage() {
  const [type, setType] = useState<BookingType>(null);
  const [step, setStep] = useState(1);
  const [form, setForm] = useState({
    service: "", city: "", address: "", date: "", time: "", notes: "", name: "", phone: "",
  });
  const [submitted, setSubmitted] = useState(false);
  const [countdown, setCountdown] = useState<number | null>(null);

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitted(true);
    if (type === "emergency") {
      let c = 30;
      setCountdown(c);
      const t = setInterval(() => {
        c--;
        setCountdown(c);
        if (c <= 0) clearInterval(t);
      }, 1000);
    }
  }

  if (submitted) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-2xl shadow-md p-10 max-w-md w-full text-center">
          {type === "emergency" ? (
            <>
              <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Zap className="w-8 h-8 text-red-500" />
              </div>
              <h2 className="text-2xl font-bold text-gray-900 mb-2">Broadcasting Job...</h2>
              {countdown !== null && countdown > 0 ? (
                <>
                  <p className="text-gray-500 mb-6">Finding the nearest verified {form.service} technician in {form.city}...</p>
                  <div className="w-24 h-24 mx-auto relative mb-6">
                    <svg className="w-24 h-24 -rotate-90" viewBox="0 0 100 100">
                      <circle cx="50" cy="50" r="40" fill="none" stroke="#fee2e2" strokeWidth="8" />
                      <circle
                        cx="50" cy="50" r="40" fill="none" stroke="#ef4444" strokeWidth="8"
                        strokeDasharray={`${(countdown / 30) * 251} 251`}
                        className="transition-all duration-1000"
                      />
                    </svg>
                    <span className="absolute inset-0 flex items-center justify-center text-3xl font-bold text-red-500">{countdown}</span>
                  </div>
                  <p className="text-sm text-gray-400">Technician has {countdown}s to accept before routing to next available</p>
                </>
              ) : (
                <>
                  <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                    <CheckCircle className="w-8 h-8 text-green-500" />
                  </div>
                  <p className="text-gray-600 mb-4">Technician matched! They are on their way.</p>
                  <p className="text-sm text-orange-500 font-semibold">ETA: ~15 minutes</p>
                </>
              )}
            </>
          ) : (
            <>
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <CheckCircle className="w-8 h-8 text-green-500" />
              </div>
              <h2 className="text-2xl font-bold text-gray-900 mb-2">Booking Confirmed!</h2>
              <p className="text-gray-500 mb-6">Your {form.service} appointment is scheduled for {form.date} at {form.time}.</p>
              <div className="bg-gray-50 rounded-xl p-4 text-left space-y-2 mb-6">
                <div className="flex justify-between text-sm"><span className="text-gray-500">Service</span><span className="font-semibold">{form.service}</span></div>
                <div className="flex justify-between text-sm"><span className="text-gray-500">Date</span><span className="font-semibold">{form.date}</span></div>
                <div className="flex justify-between text-sm"><span className="text-gray-500">Time</span><span className="font-semibold">{form.time}</span></div>
                <div className="flex justify-between text-sm"><span className="text-gray-500">Location</span><span className="font-semibold">{form.city}</span></div>
              </div>
              <p className="text-xs text-gray-400">You can reschedule up to 24 hours before the visit.</p>
            </>
          )}
          <Link href="/" className="mt-6 inline-block text-sm text-orange-500 hover:underline">← Back to Home</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b border-gray-100 sticky top-0 z-10">
        <div className="max-w-3xl mx-auto px-4 py-4 flex items-center gap-3">
          <Link href="/" className="p-2 rounded-lg hover:bg-gray-100"><ArrowLeft className="w-4 h-4" /></Link>
          <div className="flex items-center gap-2">
            <div className="w-7 h-7 bg-orange-500 rounded-lg flex items-center justify-center">
              <Wrench className="w-3.5 h-3.5 text-white" />
            </div>
            <span className="font-bold text-gray-900">YAARL <span className="text-orange-500">360</span></span>
          </div>
          <span className="text-gray-400 text-sm ml-2">/ Book a Service</span>
        </div>
      </div>

      <div className="max-w-3xl mx-auto px-4 py-8">
        {/* Step 1 — choose type */}
        {step === 1 && (
          <div>
            <h1 className="text-2xl font-extrabold text-gray-900 mb-2">What do you need?</h1>
            <p className="text-gray-500 mb-8">Choose between an instant emergency dispatch or a scheduled visit.</p>

            <div className="grid md:grid-cols-2 gap-4">
              <button
                onClick={() => { setType("emergency"); setStep(2); }}
                className="group border-2 border-red-200 hover:border-red-400 rounded-2xl p-6 text-left transition-all hover:shadow-md bg-white"
              >
                <div className="w-12 h-12 bg-red-100 rounded-xl flex items-center justify-center mb-4 group-hover:bg-red-500 transition-colors">
                  <Zap className="w-6 h-6 text-red-500 group-hover:text-white transition-colors" />
                </div>
                <h3 className="font-bold text-gray-900 text-lg mb-2">Emergency / On-Demand</h3>
                <p className="text-sm text-gray-500 leading-relaxed">Pipe burst, AC failed, electrical fault? Get a verified technician dispatched to you within minutes.</p>
                <span className="inline-block mt-4 text-xs font-semibold text-red-600 bg-red-50 px-2.5 py-1 rounded-full">
                  <Clock className="w-3 h-3 inline mr-1" />Response in ~15 min
                </span>
              </button>

              <button
                onClick={() => { setType("scheduled"); setStep(2); }}
                className="group border-2 border-blue-200 hover:border-blue-400 rounded-2xl p-6 text-left transition-all hover:shadow-md bg-white"
              >
                <div className="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center mb-4 group-hover:bg-blue-500 transition-colors">
                  <Calendar className="w-6 h-6 text-blue-500 group-hover:text-white transition-colors" />
                </div>
                <h3 className="font-bold text-gray-900 text-lg mb-2">Scheduled Visit</h3>
                <p className="text-sm text-gray-500 leading-relaxed">Book a deep clean, renovation, or painting job. Pick a date and time that works for you.</p>
                <span className="inline-block mt-4 text-xs font-semibold text-blue-600 bg-blue-50 px-2.5 py-1 rounded-full">
                  <Calendar className="w-3 h-3 inline mr-1" />Choose your slot
                </span>
              </button>
            </div>
          </div>
        )}

        {/* Step 2 — form */}
        {step === 2 && (
          <div>
            <button onClick={() => setStep(1)} className="flex items-center gap-1 text-sm text-gray-500 hover:text-orange-500 mb-6">
              <ArrowLeft className="w-4 h-4" /> Back
            </button>

            <div className="flex items-center gap-3 mb-8">
              <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${type === "emergency" ? "bg-red-100" : "bg-blue-100"}`}>
                {type === "emergency" ? <Zap className="w-5 h-5 text-red-500" /> : <Calendar className="w-5 h-5 text-blue-500" />}
              </div>
              <div>
                <h2 className="text-xl font-bold text-gray-900">
                  {type === "emergency" ? "Emergency Request" : "Schedule a Visit"}
                </h2>
                <p className="text-sm text-gray-500">Fill in the details below</p>
              </div>
            </div>

            <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-xs font-semibold text-gray-600 block mb-1">Your Name *</label>
                  <input required value={form.name} onChange={e => setForm({...form, name: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" placeholder="Saman Silva" />
                </div>
                <div>
                  <label className="text-xs font-semibold text-gray-600 block mb-1">Phone *</label>
                  <input required type="tel" value={form.phone} onChange={e => setForm({...form, phone: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" placeholder="+94 77 000 0000" />
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-gray-600 block mb-1">Service Needed *</label>
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                  {SERVICES.map(s => (
                    <button
                      type="button"
                      key={s}
                      onClick={() => setForm({...form, service: s})}
                      className={`py-2 px-3 rounded-xl text-xs font-medium border transition-all ${form.service === s ? "bg-orange-500 text-white border-orange-500" : "border-gray-200 text-gray-600 hover:border-orange-300"}`}
                    >
                      {s}
                    </button>
                  ))}
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-xs font-semibold text-gray-600 block mb-1">City *</label>
                  <div className="relative">
                    <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                    <select required value={form.city} onChange={e => setForm({...form, city: e.target.value})} className="w-full pl-9 border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300 bg-white">
                      <option value="">Select city</option>
                      {["Colombo", "Jaffna", "Kandy", "Galle", "Negombo", "Wellawatte"].map(c => <option key={c}>{c}</option>)}
                    </select>
                  </div>
                </div>
                <div>
                  <label className="text-xs font-semibold text-gray-600 block mb-1">Address *</label>
                  <input required value={form.address} onChange={e => setForm({...form, address: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" placeholder="House/Street" />
                </div>
              </div>

              {type === "scheduled" && (
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="text-xs font-semibold text-gray-600 block mb-1">Preferred Date *</label>
                    <input required type="date" value={form.date} onChange={e => setForm({...form, date: e.target.value})} min={new Date().toISOString().split("T")[0]} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300" />
                  </div>
                  <div>
                    <label className="text-xs font-semibold text-gray-600 block mb-1">Preferred Time *</label>
                    <select required value={form.time} onChange={e => setForm({...form, time: e.target.value})} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300 bg-white">
                      <option value="">Select slot</option>
                      {["8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM"].map(t => <option key={t}>{t}</option>)}
                    </select>
                  </div>
                </div>
              )}

              <div>
                <label className="text-xs font-semibold text-gray-600 block mb-1">Notes / Description</label>
                <textarea value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} rows={3} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-300 resize-none" placeholder="Describe the issue or what needs to be done..." />
              </div>

              <button
                type="submit"
                disabled={!form.service || !form.city || !form.name || !form.phone}
                className="w-full bg-orange-500 hover:bg-orange-600 disabled:bg-gray-200 disabled:text-gray-400 text-white font-semibold py-3 rounded-full transition-colors"
              >
                {type === "emergency" ? "Dispatch Emergency Technician" : "Confirm Booking"}
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}

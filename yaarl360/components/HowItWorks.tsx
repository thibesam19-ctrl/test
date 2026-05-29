import { MapPin, CheckCircle, UserCheck, Star } from "lucide-react";

const STEPS = [
  { icon: <MapPin className="w-6 h-6" />, step: "01", title: "Enter Your Location", desc: "Type your city or area. We instantly verify if services are live in your zone." },
  { icon: <CheckCircle className="w-6 h-6" />, step: "02", title: "Choose Service & Type", desc: "Pick your service. Choose Live On-Demand for emergencies or Schedule for deep cleans & renovations." },
  { icon: <UserCheck className="w-6 h-6" />, step: "03", title: "Get Matched Instantly", desc: "Our engine broadcasts to the nearest verified worker with the right skill. They accept within 30 seconds." },
  { icon: <Star className="w-6 h-6" />, step: "04", title: "Job Done, Rate & Pay", desc: "Worker arrives, completes the job. Pay securely in-app and leave a rating." },
];

export default function HowItWorks() {
  return (
    <section id="how-it-works" className="py-20 bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-14">
          <p className="text-orange-500 font-semibold text-sm uppercase tracking-widest mb-3">Simple Process</p>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-gray-900 mb-4">How YAARL 360 Works</h2>
          <p className="text-gray-500 max-w-xl mx-auto">
            From request to resolution — fast, transparent, and stress-free.
          </p>
        </div>

        <div className="grid md:grid-cols-4 gap-6 relative">
          {/* connecting line */}
          <div className="hidden md:block absolute top-10 left-[12.5%] right-[12.5%] h-0.5 bg-orange-200 z-0" />

          {STEPS.map((s, i) => (
            <div key={i} className="relative z-10 bg-white rounded-2xl p-6 border border-gray-100 shadow-sm text-center">
              <div className="w-14 h-14 bg-orange-500 rounded-2xl flex items-center justify-center text-white mx-auto mb-4 shadow-md shadow-orange-200">
                {s.icon}
              </div>
              <span className="text-xs font-bold text-orange-400 tracking-widest">{s.step}</span>
              <h3 className="font-bold text-gray-900 mt-1 mb-2">{s.title}</h3>
              <p className="text-sm text-gray-500 leading-relaxed">{s.desc}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

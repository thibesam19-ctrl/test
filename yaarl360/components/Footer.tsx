import Link from "next/link";
import { Wrench, Phone, Mail, MapPin } from "lucide-react";

export default function Footer() {
  return (
    <footer className="bg-gray-900 text-white py-14">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid md:grid-cols-4 gap-10 mb-12">
          {/* Brand */}
          <div>
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 bg-orange-500 rounded-lg flex items-center justify-center">
                <Wrench className="w-4 h-4 text-white" />
              </div>
              <span className="text-xl font-bold">YAARL <span className="text-orange-500">360</span></span>
            </div>
            <p className="text-gray-400 text-sm leading-relaxed mb-5">
              Sri Lanka&apos;s on-demand home services platform — trusted by thousands of households.
            </p>
            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm text-gray-400"><Phone className="w-4 h-4 text-orange-500" /> +94 11 000 0000</div>
              <div className="flex items-center gap-2 text-sm text-gray-400"><Mail className="w-4 h-4 text-orange-500" /> hello@yaarl360.com</div>
              <div className="flex items-center gap-2 text-sm text-gray-400"><MapPin className="w-4 h-4 text-orange-500" /> Colombo 03, Sri Lanka</div>
            </div>
          </div>

          {/* Services */}
          <div>
            <h4 className="font-bold text-sm uppercase tracking-widest text-gray-300 mb-4">Services</h4>
            <ul className="space-y-2.5">
              {["Plumbing", "Electrical", "AC Repair", "Deep Cleaning", "Painting", "Renovation", "Pest Control"].map(s => (
                <li key={s}><Link href="/book" className="text-sm text-gray-400 hover:text-orange-400 transition-colors">{s}</Link></li>
              ))}
            </ul>
          </div>

          {/* Locations */}
          <div>
            <h4 className="font-bold text-sm uppercase tracking-widest text-gray-300 mb-4">Locations</h4>
            <ul className="space-y-2.5">
              {["Colombo", "Jaffna", "Kandy", "Galle", "Negombo", "Wellawatte", "Batticaloa"].map(c => (
                <li key={c}><Link href={`/${c.toLowerCase()}/deep-cleaning`} className="text-sm text-gray-400 hover:text-orange-400 transition-colors">{c}</Link></li>
              ))}
            </ul>
          </div>

          {/* Company */}
          <div>
            <h4 className="font-bold text-sm uppercase tracking-widest text-gray-300 mb-4">Company</h4>
            <ul className="space-y-2.5">
              {["About Us", "Careers", "Blog", "Terms of Service", "Privacy Policy", "Contact"].map(l => (
                <li key={l}><Link href="#" className="text-sm text-gray-400 hover:text-orange-400 transition-colors">{l}</Link></li>
              ))}
            </ul>
          </div>
        </div>

        <div className="border-t border-white/10 pt-8 flex flex-col md:flex-row items-center justify-between gap-4">
          <p className="text-xs text-gray-500">© 2025 YAARL 360 (Pvt) Ltd. All rights reserved.</p>
          <div className="flex items-center gap-4">
            <span className="text-xs text-gray-500">Available on</span>
            <span className="bg-white/10 px-3 py-1 rounded-full text-xs text-gray-300">App Store</span>
            <span className="bg-white/10 px-3 py-1 rounded-full text-xs text-gray-300">Google Play</span>
          </div>
        </div>
      </div>
    </footer>
  );
}

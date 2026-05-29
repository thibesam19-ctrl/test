"use client";

import Navbar from "@/components/Navbar";
import HeroSection from "@/components/HeroSection";
import ServicesSection from "@/components/ServicesSection";
import HowItWorks from "@/components/HowItWorks";
import SubscriptionPlans from "@/components/SubscriptionPlans";
import B2BSection from "@/components/B2BSection";
import WorkerSection from "@/components/WorkerSection";
import Footer from "@/components/Footer";

export default function Home() {
  return (
    <main className="min-h-screen bg-white">
      <Navbar />
      <HeroSection />
      <ServicesSection />
      <HowItWorks />
      <SubscriptionPlans />
      <B2BSection />
      <WorkerSection />
      <Footer />
    </main>
  );
}

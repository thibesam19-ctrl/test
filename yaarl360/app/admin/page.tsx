"use client";

import { useState } from "react";
import { Users, Briefcase, DollarSign, TrendingUp, CheckCircle, Clock, AlertCircle, MapPin, Zap, Calendar, Star } from "lucide-react";

const MOCK_JOBS = [
  { id: "J001", customer: "Saman Silva", service: "Plumbing", city: "Colombo", type: "emergency", status: "in-progress", worker: "Kamal P.", time: "14:32", amount: 3500 },
  { id: "J002", customer: "Nimal Fernando", service: "AC Repair", city: "Jaffna", type: "emergency", status: "pending", worker: null, time: "14:45", amount: 5200 },
  { id: "J003", customer: "Priya Ratnam", service: "Deep Cleaning", city: "Kandy", type: "scheduled", status: "completed", worker: "Roshan K.", time: "10:00", amount: 8000 },
  { id: "J004", customer: "Anura Kumara", service: "Electrical", city: "Colombo", type: "emergency", status: "pending", worker: null, time: "15:01", amount: 4200 },
  { id: "J005", customer: "Chamari W.", service: "Painting", city: "Galle", type: "scheduled", status: "completed", worker: "Sunil M.", time: "09:00", amount: 15000 },
];

const MOCK_WORKERS = [
  { name: "Kamal Perera", skill: "Plumber", city: "Colombo", status: "busy", rating: 4.9, jobs: 124 },
  { name: "Roshan Kumar", skill: "Cleaner", city: "Kandy", status: "online", rating: 4.8, jobs: 89 },
  { name: "Sunil Mendis", skill: "Painter", city: "Galle", status: "offline", rating: 4.7, jobs: 67 },
  { name: "Pradeep S.", skill: "Electrician", city: "Colombo", status: "online", rating: 4.6, jobs: 201 },
  { name: "Rajan T.", skill: "AC Technician", city: "Jaffna", status: "online", rating: 4.9, jobs: 156 },
];

const STATUS_STYLES: Record<string, string> = {
  "in-progress": "bg-blue-100 text-blue-700",
  pending: "bg-yellow-100 text-yellow-700",
  completed: "bg-green-100 text-green-700",
  online: "bg-green-100 text-green-700",
  offline: "bg-gray-100 text-gray-500",
  busy: "bg-orange-100 text-orange-700",
};

export default function AdminPage() {
  const [tab, setTab] = useState<"dashboard" | "jobs" | "workers">("dashboard");
  const [jobs, setJobs] = useState(MOCK_JOBS);

  function assignWorker(jobId: string) {
    setJobs(j => j.map(job => job.id === jobId ? { ...job, status: "in-progress", worker: "Rajan T." } : job));
  }

  return (
    <div className="min-h-screen bg-gray-100 flex">
      {/* Sidebar */}
      <div className="w-60 bg-gray-900 text-white flex flex-col">
        <div className="p-5 border-b border-white/10">
          <div className="text-lg font-bold">YAARL <span className="text-orange-500">360</span></div>
          <div className="text-xs text-gray-400 mt-0.5">Super Admin Panel</div>
        </div>
        <nav className="p-3 flex-1 space-y-1">
          {[
            { id: "dashboard", label: "Dashboard", icon: <TrendingUp className="w-4 h-4" /> },
            { id: "jobs", label: "Live Dispatch", icon: <Zap className="w-4 h-4" /> },
            { id: "workers", label: "Workers", icon: <Users className="w-4 h-4" /> },
          ].map(item => (
            <button
              key={item.id}
              onClick={() => setTab(item.id as typeof tab)}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-colors ${tab === item.id ? "bg-orange-500 text-white" : "text-gray-400 hover:bg-white/10 hover:text-white"}`}
            >
              {item.icon} {item.label}
            </button>
          ))}
        </nav>
        <div className="p-4 border-t border-white/10">
          <div className="text-xs text-gray-500">Logged in as</div>
          <div className="text-sm font-semibold text-white">Admin User</div>
        </div>
      </div>

      {/* Main */}
      <div className="flex-1 overflow-auto">
        <div className="bg-white border-b border-gray-200 px-8 py-4 flex items-center justify-between">
          <h1 className="text-lg font-bold text-gray-900">
            {tab === "dashboard" && "Command Dashboard"}
            {tab === "jobs" && "Live Dispatch"}
            {tab === "workers" && "Worker Management"}
          </h1>
          <div className="flex items-center gap-2 text-sm text-gray-500">
            <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse" /> Live
          </div>
        </div>

        <div className="p-8">
          {/* Dashboard */}
          {tab === "dashboard" && (
            <div>
              <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
                {[
                  { label: "Active Jobs", value: "12", icon: <Briefcase className="w-5 h-5 text-blue-500" />, bg: "bg-blue-50", change: "+3 today" },
                  { label: "Online Workers", value: "8", icon: <Users className="w-5 h-5 text-green-500" />, bg: "bg-green-50", change: "of 15 total" },
                  { label: "Revenue Today", value: "LKR 84,200", icon: <DollarSign className="w-5 h-5 text-orange-500" />, bg: "bg-orange-50", change: "+12% vs yesterday" },
                  { label: "Active Subscribers", value: "347", icon: <TrendingUp className="w-5 h-5 text-purple-500" />, bg: "bg-purple-50", change: "MRR: LKR 1.2M" },
                ].map(s => (
                  <div key={s.label} className="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
                    <div className={`${s.bg} w-10 h-10 rounded-xl flex items-center justify-center mb-3`}>{s.icon}</div>
                    <div className="text-2xl font-extrabold text-gray-900 mb-0.5">{s.value}</div>
                    <div className="text-xs text-gray-500">{s.label}</div>
                    <div className="text-xs text-green-600 font-medium mt-1">{s.change}</div>
                  </div>
                ))}
              </div>

              <div className="bg-white rounded-2xl border border-gray-100 p-6">
                <h3 className="font-bold text-gray-900 mb-4">Recent Jobs</h3>
                <div className="space-y-3">
                  {jobs.slice(0, 4).map(job => (
                    <div key={job.id} className="flex items-center justify-between py-3 border-b border-gray-50 last:border-0">
                      <div className="flex items-center gap-3">
                        <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${job.type === "emergency" ? "bg-red-100" : "bg-blue-100"}`}>
                          {job.type === "emergency" ? <Zap className="w-4 h-4 text-red-500" /> : <Calendar className="w-4 h-4 text-blue-500" />}
                        </div>
                        <div>
                          <div className="text-sm font-semibold text-gray-900">{job.customer}</div>
                          <div className="text-xs text-gray-500">{job.service} · {job.city}</div>
                        </div>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className={`text-xs font-semibold px-2 py-1 rounded-full ${STATUS_STYLES[job.status]}`}>{job.status}</span>
                        <span className="text-sm font-bold text-gray-900">LKR {job.amount.toLocaleString()}</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* Live Dispatch */}
          {tab === "jobs" && (
            <div>
              <div className="flex items-center gap-3 mb-6">
                <div className="flex items-center gap-2 text-sm">
                  <span className="w-2 h-2 bg-yellow-500 rounded-full" /> Pending: {jobs.filter(j => j.status === "pending").length}
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <span className="w-2 h-2 bg-blue-500 rounded-full" /> In Progress: {jobs.filter(j => j.status === "in-progress").length}
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <span className="w-2 h-2 bg-green-500 rounded-full" /> Completed: {jobs.filter(j => j.status === "completed").length}
                </div>
              </div>

              <div className="space-y-3">
                {jobs.map(job => (
                  <div key={job.id} className="bg-white rounded-2xl border border-gray-100 p-5 shadow-sm">
                    <div className="flex items-start justify-between gap-4">
                      <div className="flex items-start gap-4">
                        <div className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 ${job.type === "emergency" ? "bg-red-100" : "bg-blue-100"}`}>
                          {job.type === "emergency" ? <Zap className="w-5 h-5 text-red-500" /> : <Calendar className="w-5 h-5 text-blue-500" />}
                        </div>
                        <div>
                          <div className="flex items-center gap-2 mb-1">
                            <span className="font-bold text-gray-900">{job.customer}</span>
                            <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${STATUS_STYLES[job.status]}`}>{job.status}</span>
                          </div>
                          <div className="text-sm text-gray-600">{job.service}</div>
                          <div className="flex items-center gap-3 mt-1.5 text-xs text-gray-500">
                            <span className="flex items-center gap-1"><MapPin className="w-3 h-3" /> {job.city}</span>
                            <span className="flex items-center gap-1"><Clock className="w-3 h-3" /> {job.time}</span>
                            <span className="flex items-center gap-1"><DollarSign className="w-3 h-3" /> LKR {job.amount.toLocaleString()}</span>
                          </div>
                          {job.worker && <div className="mt-1.5 text-xs text-blue-600 font-semibold">Worker: {job.worker}</div>}
                        </div>
                      </div>
                      {job.status === "pending" && (
                        <button
                          onClick={() => assignWorker(job.id)}
                          className="flex-shrink-0 bg-orange-500 hover:bg-orange-600 text-white text-xs font-semibold px-3 py-2 rounded-lg transition-colors"
                        >
                          Manual Assign
                        </button>
                      )}
                      {job.status === "completed" && (
                        <CheckCircle className="w-5 h-5 text-green-500 flex-shrink-0" />
                      )}
                      {job.status === "in-progress" && (
                        <AlertCircle className="w-5 h-5 text-blue-500 flex-shrink-0" />
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Workers */}
          {tab === "workers" && (
            <div>
              <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
                {MOCK_WORKERS.map(w => (
                  <div key={w.name} className="bg-white rounded-2xl border border-gray-100 p-5 shadow-sm">
                    <div className="flex items-start justify-between mb-4">
                      <div>
                        <div className="font-bold text-gray-900">{w.name}</div>
                        <div className="text-sm text-gray-500">{w.skill} · {w.city}</div>
                      </div>
                      <span className={`text-xs font-semibold px-2 py-1 rounded-full ${STATUS_STYLES[w.status]}`}>{w.status}</span>
                    </div>
                    <div className="flex items-center justify-between text-sm">
                      <div className="flex items-center gap-1 text-yellow-500">
                        <Star className="w-4 h-4 fill-yellow-400" />
                        <span className="font-semibold text-gray-900">{w.rating}</span>
                      </div>
                      <span className="text-gray-500">{w.jobs} jobs completed</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

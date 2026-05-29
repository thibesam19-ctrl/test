"use client";

import { useState, useEffect } from "react";
import { Zap, MapPin, DollarSign, Clock, CheckCircle, XCircle, Wrench, Star, TrendingUp } from "lucide-react";

type JobRequest = {
  id: string;
  service: string;
  customer: string;
  distance: string;
  payout: number;
  issue: string;
  city: string;
};

const MOCK_JOB: JobRequest = {
  id: "J-2045",
  service: "Plumbing",
  customer: "Saman Silva",
  distance: "2.4 km away",
  payout: 2800,
  issue: "Pipe burst under kitchen sink. Water leaking badly.",
  city: "Colombo 5",
};

export default function WorkerApp() {
  const [online, setOnline] = useState(false);
  const [job, setJob] = useState<JobRequest | null>(null);
  const [countdown, setCountdown] = useState(30);
  const [jobStatus, setJobStatus] = useState<"accepted" | "rejected" | null>(null);
  const [earnings, setEarnings] = useState(8400);

  useEffect(() => {
    if (!online) { setJob(null); setJobStatus(null); return; }
    const t = setTimeout(() => setJob(MOCK_JOB), 3000);
    return () => clearTimeout(t);
  }, [online]);

  useEffect(() => {
    if (!job || jobStatus) return;
    setCountdown(30);
    const interval = setInterval(() => {
      setCountdown(c => {
        if (c <= 1) {
          clearInterval(interval);
          setJob(null);
          return 0;
        }
        return c - 1;
      });
    }, 1000);
    return () => clearInterval(interval);
  }, [job, jobStatus]);

  function accept() {
    setJobStatus("accepted");
    setEarnings(e => e + MOCK_JOB.payout);
  }

  function reject() {
    setJobStatus("rejected");
    setTimeout(() => { setJob(null); setJobStatus(null); }, 1500);
  }

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      {/* Simulated phone frame */}
      <div className="w-full max-w-sm bg-white rounded-3xl shadow-2xl overflow-hidden border border-gray-200">
        {/* Status bar */}
        <div className="bg-gray-900 px-5 py-2.5 flex items-center justify-between">
          <span className="text-white text-xs font-medium">9:41 AM</span>
          <div className="flex items-center gap-2">
            <div className="w-7 h-7 bg-orange-500 rounded-lg flex items-center justify-center">
              <Wrench className="w-3.5 h-3.5 text-white" />
            </div>
            <span className="text-white text-sm font-bold">YAARL Worker</span>
          </div>
          <span className="text-white text-xs">100%</span>
        </div>

        <div className="p-5 space-y-5">
          {/* Profile */}
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 bg-orange-100 rounded-full flex items-center justify-center text-orange-600 font-bold text-lg">K</div>
            <div className="flex-1">
              <div className="font-bold text-gray-900">Kamal Perera</div>
              <div className="flex items-center gap-1 text-xs text-gray-500">
                <Star className="w-3 h-3 fill-yellow-400 text-yellow-400" /> 4.9 · Plumber · Colombo
              </div>
            </div>
          </div>

          {/* Online toggle */}
          <div className={`rounded-2xl p-4 flex items-center justify-between transition-all ${online ? "bg-green-50 border border-green-200" : "bg-gray-50 border border-gray-200"}`}>
            <div>
              <div className={`font-bold text-sm ${online ? "text-green-700" : "text-gray-600"}`}>
                {online ? "You are ONLINE" : "You are OFFLINE"}
              </div>
              <div className="text-xs text-gray-500 mt-0.5">
                {online ? "Accepting emergency jobs in your area" : "Toggle to start receiving jobs"}
              </div>
            </div>
            <button
              onClick={() => { setOnline(!online); setJob(null); setJobStatus(null); }}
              className={`relative w-14 h-7 rounded-full transition-all ${online ? "bg-green-500" : "bg-gray-300"}`}
            >
              <div className={`absolute top-0.5 w-6 h-6 bg-white rounded-full shadow transition-all ${online ? "left-7.5 translate-x-[calc(100%-1px)]" : "left-0.5"}`} />
            </button>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-3 gap-2">
            {[
              { label: "Today", value: `LKR ${earnings.toLocaleString()}`, icon: <DollarSign className="w-3.5 h-3.5 text-green-500" /> },
              { label: "Jobs", value: "5", icon: <CheckCircle className="w-3.5 h-3.5 text-blue-500" /> },
              { label: "Rating", value: "4.9★", icon: <Star className="w-3.5 h-3.5 text-yellow-500" /> },
            ].map(s => (
              <div key={s.label} className="bg-gray-50 rounded-xl p-3 text-center">
                <div className="flex justify-center mb-1">{s.icon}</div>
                <div className="font-bold text-gray-900 text-sm">{s.value}</div>
                <div className="text-xs text-gray-500">{s.label}</div>
              </div>
            ))}
          </div>

          {/* Waiting animation */}
          {online && !job && !jobStatus && (
            <div className="bg-green-50 border border-green-200 rounded-2xl p-6 text-center">
              <div className="flex justify-center mb-3">
                <div className="w-12 h-12 border-4 border-green-500 border-t-transparent rounded-full animate-spin" />
              </div>
              <p className="text-sm font-semibold text-green-700">Scanning for nearby jobs...</p>
              <p className="text-xs text-gray-500 mt-1">A job request will appear shortly</p>
            </div>
          )}

          {/* Job request popup */}
          {job && !jobStatus && (
            <div className="bg-white border-2 border-orange-400 rounded-2xl shadow-lg overflow-hidden">
              <div className="bg-orange-500 px-4 py-2.5 flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Zap className="w-4 h-4 text-white" />
                  <span className="text-white text-sm font-bold">Emergency Job Request</span>
                </div>
                <div className="flex items-center gap-1">
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${countdown <= 10 ? "bg-red-100 text-red-600" : "bg-white/20 text-white"}`}>
                    {countdown}
                  </div>
                </div>
              </div>

              <div className="p-4 space-y-3">
                <div className="flex justify-between items-start">
                  <div>
                    <div className="font-bold text-gray-900">{job.service}</div>
                    <div className="text-xs text-gray-500">{job.customer}</div>
                  </div>
                  <div className="text-right">
                    <div className="text-lg font-extrabold text-green-600">LKR {job.payout.toLocaleString()}</div>
                    <div className="text-xs text-gray-400">your earnings</div>
                  </div>
                </div>

                <div className="bg-gray-50 rounded-xl p-3 text-xs text-gray-600 leading-relaxed">
                  &ldquo;{job.issue}&rdquo;
                </div>

                <div className="flex items-center gap-4 text-xs text-gray-500">
                  <span className="flex items-center gap-1"><MapPin className="w-3 h-3" /> {job.city}</span>
                  <span className="flex items-center gap-1"><Clock className="w-3 h-3" /> {job.distance}</span>
                </div>

                <div className="grid grid-cols-2 gap-2 pt-1">
                  <button
                    onClick={reject}
                    className="flex items-center justify-center gap-2 border-2 border-red-200 text-red-500 hover:bg-red-50 font-semibold py-2.5 rounded-xl text-sm transition-colors"
                  >
                    <XCircle className="w-4 h-4" /> Reject
                  </button>
                  <button
                    onClick={accept}
                    className="flex items-center justify-center gap-2 bg-green-500 hover:bg-green-600 text-white font-semibold py-2.5 rounded-xl text-sm transition-colors"
                  >
                    <CheckCircle className="w-4 h-4" /> Accept
                  </button>
                </div>
              </div>
            </div>
          )}

          {/* Accepted state */}
          {jobStatus === "accepted" && (
            <div className="bg-green-50 border border-green-200 rounded-2xl p-5 text-center">
              <CheckCircle className="w-10 h-10 text-green-500 mx-auto mb-3" />
              <div className="font-bold text-green-700 mb-1">Job Accepted!</div>
              <div className="text-sm text-gray-600 mb-2">Navigate to {MOCK_JOB.city}</div>
              <div className="text-xs text-gray-500">Customer has been notified. ETA tracking is live.</div>
              <div className="mt-3 flex items-center justify-center gap-1 text-sm font-bold text-green-600">
                <TrendingUp className="w-4 h-4" />+LKR {MOCK_JOB.payout.toLocaleString()} added
              </div>
            </div>
          )}

          {/* Rejected state */}
          {jobStatus === "rejected" && (
            <div className="bg-red-50 border border-red-200 rounded-2xl p-4 text-center">
              <XCircle className="w-8 h-8 text-red-400 mx-auto mb-2" />
              <div className="text-sm text-red-600 font-medium">Job passed to next technician</div>
            </div>
          )}
        </div>

        <div className="bg-gray-50 border-t border-gray-100 px-5 py-3 text-center">
          <p className="text-xs text-gray-400">YAARL 360 Worker App Demo</p>
        </div>
      </div>
    </div>
  );
}

import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { ArrowRight, ShieldCheck, Truck, Zap } from 'lucide-react';
import { ProductSections } from '../components/ProductSections.jsx';

export function Home() {
  return (
    <>
      <section className="relative min-h-[78vh] overflow-hidden bg-ink text-white">
        <img className="absolute inset-0 h-full w-full object-cover opacity-45" src="https://images.unsplash.com/photo-1441986300917-64674bd600d8" alt="Premium shopping floor" />
        <div className="relative mx-auto grid max-w-7xl gap-8 px-4 py-20 md:grid-cols-[1.1fr_.9fr] md:py-28">
          <motion.div initial={{ opacity: 0, y: 24 }} animate={{ opacity: 1, y: 0 }} className="max-w-2xl">
            <h1 className="text-5xl font-black leading-tight md:text-7xl">SwiftCart</h1>
            <p className="mt-5 max-w-xl text-lg text-white/80">Premium electronics, fashion, lifestyle essentials, fast checkout, tracked delivery, and a refined shopping experience built for scale.</p>
            <Link to="/products" className="mt-8 inline-flex items-center gap-2 rounded-md bg-white px-5 py-3 text-sm font-black text-ink">
              Shop collection <ArrowRight size={18} />
            </Link>
          </motion.div>
          <div className="grid content-end gap-3 md:justify-end">
            {[['Fast delivery', Truck], ['Secure payments', ShieldCheck], ['Top deals', Zap]].map(([label, Icon]) => (
              <div key={label} className="flex items-center gap-3 rounded-md bg-white/10 px-4 py-3 backdrop-blur">
                <Icon size={20} />
                <span className="font-bold">{label}</span>
              </div>
            ))}
          </div>
        </div>
      </section>
      <ProductSections />
    </>
  );
}

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Search } from 'lucide-react';
import { catalogService } from '../services/catalogService.js';
import { ProductCard } from '../components/ProductCard.jsx';
import { SkeletonGrid } from '../components/SkeletonGrid.jsx';

export function ProductListing({ embedded = false }) {
  const [query, setQuery] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const { data: categories = [] } = useQuery({ queryKey: ['categories'], queryFn: catalogService.categories, retry: false });
  const { data, isLoading } = useQuery({
    queryKey: ['products', query, categoryId],
    queryFn: () => catalogService.products({ size: 60, query: query || undefined, categoryId: categoryId || undefined }),
    retry: false
  });

  const products = Array.isArray(data) 
  ? data 
  : (data?.content ?? data?.data ?? []);

  return (
    <section className="mx-auto max-w-7xl px-4 py-10">
      <div className="mb-6 flex flex-wrap items-end justify-between gap-4">
        <div>
          <h2 className="text-3xl font-black">{embedded ? 'Trending now' : 'Product catalog'}</h2>
          <p className="mt-1 text-sm text-black/60 dark:text-white/60">Search by product or brand, then filter by category.</p>
        </div>
        <div className="grid w-full gap-2 sm:w-auto sm:grid-cols-[260px_220px]">
          <label className="flex items-center gap-2 rounded-md border bg-white px-3 py-2 text-sm dark:bg-white/10">
            <Search size={16} />
            <input value={query} onChange={(event) => setQuery(event.target.value)} className="w-full bg-transparent outline-none" placeholder="Search name or brand" />
          </label>
          <select value={categoryId} onChange={(event) => setCategoryId(event.target.value)} className="rounded-md border bg-white px-3 py-2 text-sm dark:bg-white/10">
            <option value="">All categories</option>
            {categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}
          </select>
        </div>
      </div>
      {isLoading ? <SkeletonGrid /> : (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {products.map((product) => <ProductCard key={product.id} product={product} />)}
        </div>
      )}
      {!isLoading && products.length === 0 && <div className="rounded-lg bg-white p-6 text-sm font-semibold dark:bg-white/5">No products found.</div>}
    </section>
  );
}

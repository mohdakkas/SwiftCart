import { useQuery } from '@tanstack/react-query';
import { catalogService } from '../services/catalogService.js';
import { ProductCard } from '../components/ProductCard.jsx';
import { SkeletonGrid } from '../components/SkeletonGrid.jsx';

const fallbackProducts = [
  { id: 1, name: 'SwiftPhone X1', category: 'Mobiles', price: 69999, salePrice: 62999, averageRating: 4.7, images: ['https://images.unsplash.com/photo-1511707171634-5f897ff02aa9'] },
  { id: 2, name: 'SwiftBuds Pro', category: 'Electronics', price: 14999, salePrice: 11999, averageRating: 4.5, images: ['https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1'] },
  { id: 3, name: 'RunAir Knit Sneakers', category: 'Sneakers', price: 6499, salePrice: 4999, averageRating: 4.4, images: ['https://images.unsplash.com/photo-1542291026-7eec264c27ff'] }
];

export function ProductListing({ embedded = false }) {
  const { data, isLoading } = useQuery({
    queryKey: ['products'],
    queryFn: () => catalogService.products({ size: 12 }),
    retry: false
  });

  const products = data?.content?.length ? data.content : fallbackProducts;

  return (
    <section className="mx-auto max-w-7xl px-4 py-10">
      <div className="mb-6 flex flex-wrap items-end justify-between gap-4">
        <div>
          <h2 className="text-3xl font-black">{embedded ? 'Trending now' : 'Product catalog'}</h2>
          <p className="mt-1 text-sm text-black/60 dark:text-white/60">Search, filter, sort, paginate, and manage stock from the API layer.</p>
        </div>
        <div className="flex gap-2">
          <select className="rounded-md border bg-white px-3 py-2 text-sm dark:bg-white/10"><option>Sort: Featured</option><option>Price: Low to High</option></select>
          <select className="rounded-md border bg-white px-3 py-2 text-sm dark:bg-white/10"><option>All categories</option><option>Electronics</option></select>
        </div>
      </div>
      {isLoading ? <SkeletonGrid /> : <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">{products.map((product) => <ProductCard key={product.id} product={product} />)}</div>}
    </section>
  );
}

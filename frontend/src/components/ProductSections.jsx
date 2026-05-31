import { useQuery } from '@tanstack/react-query';
import { catalogService } from '../services/catalogService.js';
import { ProductCard } from './ProductCard.jsx';
import { SkeletonGrid } from './SkeletonGrid.jsx';

const sectionConfig = [
  { title: 'Trending Products', filter: (product) => product.trending },
  { title: 'Best Sellers', filter: (product) => product.bestSeller },
  { title: 'New Arrivals', filter: (product) => product.newArrival },
  { title: 'Featured Products', filter: (product) => product.featured },
  { title: 'Top Rated Products', filter: (product) => Number(product.averageRating) >= 4.6 },
  { title: 'Recommended Products', filter: (_product, index) => index % 5 === 0 }
];

export function ProductSections() {
  const { data, isLoading } = useQuery({
    queryKey: ['home-product-sections'],
    queryFn: () => catalogService.products({ size: 240 }),
    retry: false
  });

  const products = data?.content ?? [];

  if (isLoading) {
    return <section className="mx-auto max-w-7xl px-4 py-10"><SkeletonGrid /></section>;
  }

  return (
    <div className="mx-auto max-w-7xl px-4 py-10">
      {sectionConfig.map((section) => {
        const selected = products.filter(section.filter).slice(0, 8);
        if (selected.length === 0) return null;
        return (
          <section key={section.title} className="mb-12">
            <div className="mb-5">
              <h2 className="text-3xl font-black">{section.title}</h2>
              <p className="mt-1 text-sm text-black/60 dark:text-white/60">Curated from SwiftCart live product catalog.</p>
            </div>
            <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
              {selected.map((product) => <ProductCard key={`${section.title}-${product.id}`} product={product} />)}
            </div>
          </section>
        );
      })}
    </div>
  );
}

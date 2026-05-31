import { useQuery } from '@tanstack/react-query';
import { catalogService } from '../services/catalogService.js';

const fallback = [
  { id: 1, name: 'Electronics', imageUrl: 'https://images.unsplash.com/photo-1498049794561-7780e7231661' },
  { id: 2, name: 'Fashion', imageUrl: 'https://images.unsplash.com/photo-1445205170230-053b83016050' },
  { id: 3, name: 'Sneakers', imageUrl: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff' }
];

export function Categories() {
  const { data } = useQuery({ queryKey: ['categories'], queryFn: catalogService.categories, retry: false });
  const categories = Array.isArray(data) && data.length ? data : fallback;
  return (
    <section className="mx-auto max-w-7xl px-4 py-10">
      <h1 className="mb-6 text-3xl font-black">Categories</h1>
      <div className="grid gap-5 md:grid-cols-3">
        {categories.map((category) => (
          <article key={category.id} className="relative overflow-hidden rounded-lg bg-ink text-white">
            <img src={category.imageUrl} alt={category.name} className="h-64 w-full object-cover opacity-70" />
            <div className="absolute inset-x-0 bottom-0 p-5 text-2xl font-black">{category.name}</div>
          </article>
        ))}
      </div>
    </section>
  );
}

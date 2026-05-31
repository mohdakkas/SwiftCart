import { ProductCard } from '../components/ProductCard.jsx';

const items = [
  { id: 2, name: 'SwiftBuds Pro', category: 'Electronics', price: 14999, salePrice: 11999, averageRating: 4.5, images: ['https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1'] }
];

export function Wishlist() {
  return (
    <section className="mx-auto max-w-7xl px-4 py-10">
      <h1 className="mb-6 text-3xl font-black">Wishlist</h1>
      <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {items.map((item) => <ProductCard key={item.id} product={item} />)}
      </div>
    </section>
  );
}

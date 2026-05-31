import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Star, Truck } from 'lucide-react';
import { catalogService } from '../services/catalogService.js';
import { Button } from '../components/Button.jsx';
import { ProductReviews } from '../components/ProductReviews.jsx';
import { StarRating } from '../components/StarRating.jsx';

export function ProductDetails() {
  const { id } = useParams();
  const { data } = useQuery({ queryKey: ['product', id], queryFn: () => catalogService.product(id), retry: false });
  const [selectedImage, setSelectedImage] = useState('');
  const product = data ?? {
    name: 'SwiftPhone X1',
    description: 'Flagship phone with OLED display, fast charging, and pro-grade camera.',
    price: 69999,
    salePrice: 62999,
    averageRating: 4.7,
    reviewCount: 0,
    stockQuantity: 75,
    images: ['https://images.unsplash.com/photo-1511707171634-5f897ff02aa9']
  };
  const galleryImages = product.productImages?.length
    ? product.productImages.map((image) => image.imageUrl)
    : [product.imageUrl, ...(product.images ?? [])].filter(Boolean);
  const mainImage = selectedImage || product.imageUrl || galleryImages[0];

  useEffect(() => {
    setSelectedImage('');
  }, [id, product.imageUrl]);

  return (
    <>
      <section className="mx-auto grid max-w-7xl gap-8 px-4 py-10 lg:grid-cols-2">
        <div>
          <div className="overflow-hidden rounded-lg bg-white shadow-soft dark:bg-white/5">
            <img src={mainImage} alt={product.name} className="aspect-square w-full object-cover" />
          </div>
          {galleryImages.length > 1 && (
            <div className="mt-4 flex gap-3 overflow-x-auto pb-1">
              {galleryImages.map((imageUrl) => (
                <button
                  type="button"
                  key={imageUrl}
                  onClick={() => setSelectedImage(imageUrl)}
                  className={`h-20 w-20 shrink-0 overflow-hidden rounded-md border ${mainImage === imageUrl ? 'border-signal ring-2 ring-signal/25' : 'border-black/10 dark:border-white/10'}`}
                  aria-label="View product image"
                >
                  <img src={imageUrl} alt={product.name} className="h-full w-full object-cover" />
                </button>
              ))}
            </div>
          )}
        </div>
        <div className="py-4">
          <div className="mb-3 inline-flex items-center gap-2 rounded-full bg-signal/10 px-3 py-1 text-sm font-bold text-signal">
            <Star size={16} /> {Number(product.averageRating ?? 0).toFixed(1)} ({product.reviewCount ?? 0} reviews)
          </div>
          <h1 className="text-4xl font-black">{product.name}</h1>
          <div className="mt-3 flex items-center gap-2">
            <StarRating value={product.averageRating} readOnly size={18} />
            <span className="text-sm font-semibold text-black/55 dark:text-white/60">{product.reviewCount ?? 0} customer reviews</span>
          </div>
          <p className="mt-4 text-black/65 dark:text-white/70">{product.description}</p>
          <div className="mt-6 flex items-end gap-3">
            <span className="text-4xl font-black">Rs. {Number(product.salePrice ?? product.price).toLocaleString('en-IN')}</span>
            {product.salePrice && <span className="pb-1 text-black/45 line-through">Rs. {Number(product.price).toLocaleString('en-IN')}</span>}
          </div>
          <div className="mt-6 flex items-center gap-2 text-sm font-semibold"><Truck size={18} /> Free delivery on eligible orders</div>
          <div className="mt-8 flex gap-3">
            <Button>Add to cart</Button>
            <Button className="bg-coral text-white dark:bg-coral dark:text-white">Buy now</Button>
          </div>
        </div>
      </section>
      {product.id && <ProductReviews product={product} />}
    </>
  );
}

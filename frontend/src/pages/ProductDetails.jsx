import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useSelector } from 'react-redux';
import { toast } from 'react-toastify';
import { ShoppingBag, Star, Truck, Zap } from 'lucide-react';
import { catalogService } from '../services/catalogService.js';
import { cartService } from '../services/cartService.js';
import { Button } from '../components/Button.jsx';
import { ProductReviews } from '../components/ProductReviews.jsx';
import { StarRating } from '../components/StarRating.jsx';

export function ProductDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const accessToken = useSelector((state) => state.auth.accessToken);
  const { data, isLoading, isError } = useQuery({ queryKey: ['product', id], queryFn: () => catalogService.product(id), retry: false });
  const [selectedImage, setSelectedImage] = useState('');

  const product = data;
  const galleryImages = product?.productImages?.length
    ? product.productImages.map((image) => image.imageUrl)
    : [product?.imageUrl, ...(product?.images ?? [])].filter(Boolean);
  const mainImage = selectedImage || product?.imageUrl || galleryImages[0] || 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3';
  const outOfStock = Number(product?.stockQuantity ?? 0) <= 0;

  const addToCart = useMutation({
    mutationFn: () => cartService.add({ productId: product.id, quantity: 1 }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      toast.success('Added to cart');
    },
    onError: (error) => toast.error(error.response?.data?.message ?? 'Could not add item to cart')
  });

  useEffect(() => {
    setSelectedImage('');
  }, [id, product?.imageUrl]);

  const requireLogin = () => {
    if (!accessToken) {
      navigate('/login', { state: { from: `/products/${id}` } });
      return false;
    }
    return true;
  };

  const handleAddToCart = () => {
    if (!product || !requireLogin()) {
      return;
    }
    addToCart.mutate();
  };

  const handleBuyNow = () => {
    if (!product || !requireLogin()) {
      return;
    }
    addToCart.mutate(undefined, {
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ['cart'] });
        navigate('/checkout');
      }
    });
  };

  if (isLoading) {
    return <section className="mx-auto max-w-7xl px-4 py-10"><div className="h-96 animate-pulse rounded-lg bg-white shadow-soft dark:bg-white/5" /></section>;
  }

  if (isError || !product) {
    return <section className="mx-auto max-w-3xl px-4 py-10"><div className="rounded-lg bg-red-50 p-6 font-semibold text-red-700">Product could not be loaded.</div></section>;
  }

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
          <div className="mt-4 text-sm font-bold text-black/60 dark:text-white/65">{outOfStock ? 'Out of stock' : `${product.stockQuantity} item(s) available`}</div>
          <div className="mt-6 flex items-center gap-2 text-sm font-semibold"><Truck size={18} /> Free delivery on eligible orders</div>
          <div className="mt-8 flex flex-wrap gap-3">
            <Button onClick={handleAddToCart} disabled={addToCart.isPending || outOfStock}><ShoppingBag size={18} /> Add to cart</Button>
            <Button onClick={handleBuyNow} disabled={addToCart.isPending || outOfStock} className="bg-coral text-white dark:bg-coral dark:text-white"><Zap size={18} /> Buy now</Button>
          </div>
        </div>
      </section>
      <ProductReviews product={product} />
    </>
  );
}

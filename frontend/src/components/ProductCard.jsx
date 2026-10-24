import { motion } from 'framer-motion';
import { Heart, ShoppingBag, Zap } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useSelector } from 'react-redux';
import { toast } from 'react-toastify';
import { Button } from './Button.jsx';
import { cartService } from '../services/cartService.js';

export function ProductCard({ product }) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const accessToken = useSelector((state) => state.auth.accessToken);
  const image = product.imageUrl ?? product.images?.[0] ?? 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3';
  const price = product.salePrice ?? product.price;

  const addToCart = useMutation({
    mutationFn: () => cartService.add({ productId: product.id, quantity: 1 }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      toast.success('Added to cart');
    },
    onError: (error) => toast.error(error.response?.data?.message ?? 'Could not add item to cart')
  });

  const requireLogin = () => {
    if (!accessToken) {
      navigate('/login', { state: { from: `/products/${product.id}` } });
      return false;
    }
    return true;
  };

  const handleAddToCart = () => {
    if (!requireLogin()) {
      return;
    }
    addToCart.mutate();
  };

  const handleBuyNow = () => {
    if (!requireLogin()) {
      return;
    }
    addToCart.mutate(undefined, {
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ['cart'] });
        navigate('/checkout');
      }
    });
  };

  const outOfStock = Number(product.stockQuantity ?? 0) <= 0;

  return (
    <motion.article whileHover={{ y: -4 }} className="overflow-hidden rounded-lg border border-black/10 bg-white shadow-sm dark:border-white/10 dark:bg-white/5">
      <Link to={`/products/${product.id}`} className="relative block aspect-[4/3] overflow-hidden bg-black/5">
        {product.badge && <span className="absolute left-3 top-3 z-10 rounded bg-coral px-2 py-1 text-[11px] font-black uppercase text-white">{product.badge}</span>}
        <img src={image} alt={product.name} className="h-full w-full object-cover transition duration-500 hover:scale-105" />
      </Link>
      <div className="grid gap-3 p-4">
        <div>
          <div className="text-xs font-bold uppercase text-signal">{product.category}</div>
          <div className="text-xs font-semibold text-black/50 dark:text-white/50">{product.brand}</div>
          <Link to={`/products/${product.id}`} className="line-clamp-2 text-base font-black">{product.name}</Link>
          <div className="mt-1 text-sm text-black/60 dark:text-white/60">{Number(product.averageRating ?? 0).toFixed(1)} rating</div>
        </div>
        <div className="flex items-center justify-between">
          <div>
            <span className="text-lg font-black">Rs. {Number(price).toLocaleString('en-IN')}</span>
            {product.salePrice && <span className="ml-2 text-xs text-black/50 line-through">Rs. {Number(product.price).toLocaleString('en-IN')}</span>}
          </div>
          <button aria-label="Add to wishlist" className="rounded-full p-2 hover:bg-black/5 dark:hover:bg-white/10"><Heart size={18} /></button>
        </div>
        <div className="grid grid-cols-2 gap-2">
          <Button onClick={handleAddToCart} disabled={addToCart.isPending || outOfStock}><ShoppingBag size={16} /> {outOfStock ? 'Out' : 'Cart'}</Button>
          <Button onClick={handleBuyNow} disabled={addToCart.isPending || outOfStock} className="bg-coral text-white dark:bg-coral dark:text-white"><Zap size={16} /> Buy</Button>
        </div>
      </div>
    </motion.article>
  );
}

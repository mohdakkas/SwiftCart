import { motion } from 'framer-motion';
import { Heart, ShoppingBag } from 'lucide-react';
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
  const image = product.images?.[0] ?? 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3';
  const price = product.salePrice ?? product.price;
  const addToCart = useMutation({
    mutationFn: () => cartService.add({ productId: product.id, quantity: 1 }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      toast.success('Added to cart');
    },
    onError: () => toast.error('Could not add item to cart')
  });

  const handleAddToCart = () => {
    if (!accessToken) {
      navigate('/login');
      return;
    }
    addToCart.mutate();
  };

  return (
    <motion.article whileHover={{ y: -4 }} className="overflow-hidden rounded-lg border border-black/10 bg-white shadow-sm dark:border-white/10 dark:bg-white/5">
      <Link to={`/products/${product.id}`} className="relative block aspect-[4/3] overflow-hidden bg-black/5">
        {product.badge && <span className="absolute left-3 top-3 z-10 rounded bg-coral px-2 py-1 text-[11px] font-black uppercase text-white">{product.badge}</span>}
        <img src={image} alt={product.name} className="h-full w-full object-cover transition duration-500 hover:scale-105" />
      </Link>
      <div className="grid gap-3 p-4">
        <div>
          <div className="text-xs font-bold uppercase text-signal">{product.category}</div>
          <Link to={`/products/${product.id}`} className="line-clamp-2 text-base font-black">{product.name}</Link>
          <div className="mt-1 text-sm text-black/60 dark:text-white/60">{product.averageRating} rating</div>
        </div>
        <div className="flex items-center justify-between">
          <div>
            <span className="text-lg font-black">Rs. {Number(price).toLocaleString('en-IN')}</span>
            {product.salePrice && <span className="ml-2 text-xs text-black/50 line-through">Rs. {Number(product.price).toLocaleString('en-IN')}</span>}
          </div>
          <button aria-label="Add to wishlist" className="rounded-full p-2 hover:bg-black/5 dark:hover:bg-white/10"><Heart size={18} /></button>
        </div>
        <Button onClick={handleAddToCart} disabled={addToCart.isPending}><ShoppingBag size={16} /> Add</Button>
      </div>
    </motion.article>
  );
}

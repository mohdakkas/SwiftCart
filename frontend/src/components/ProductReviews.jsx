import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { toast } from 'react-toastify';
import { Edit3, Trash2 } from 'lucide-react';
import { Button } from './Button.jsx';
import { StarRating } from './StarRating.jsx';
import { reviewService } from '../services/reviewService.js';

const emptyForm = { rating: 5, title: '', comment: '' };

export function ProductReviews({ product }) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { accessToken, user, role } = useSelector((state) => state.auth);
  const [form, setForm] = useState(emptyForm);
  const [editingReviewId, setEditingReviewId] = useState(null);
  const productId = product.id;

  const { data: reviews = [], isLoading } = useQuery({
    queryKey: ['product-reviews', productId],
    queryFn: () => reviewService.list(productId),
    enabled: Boolean(productId),
    retry: false
  });

  const currentUserReview = useMemo(
    () => reviews.find((review) => review.userEmail === user?.email),
    [reviews, user?.email]
  );

  const invalidateProductReviews = () => {
    queryClient.invalidateQueries({ queryKey: ['product-reviews', productId] });
    queryClient.invalidateQueries({ queryKey: ['product', String(productId)] });
    queryClient.invalidateQueries({ queryKey: ['product', productId] });
    queryClient.invalidateQueries({ queryKey: ['products'] });
  };

  const createReview = useMutation({
    mutationFn: (payload) => reviewService.create(payload),
    onSuccess: () => {
      setForm(emptyForm);
      invalidateProductReviews();
      toast.success('Review submitted');
    },
    onError: (error) => toast.error(error.response?.data?.message ?? 'Could not submit review')
  });

  const updateReview = useMutation({
    mutationFn: ({ reviewId, payload }) => reviewService.update(reviewId, payload),
    onSuccess: () => {
      setEditingReviewId(null);
      setForm(emptyForm);
      invalidateProductReviews();
      toast.success('Review updated');
    },
    onError: (error) => toast.error(error.response?.data?.message ?? 'Could not update review')
  });

  const deleteReview = useMutation({
    mutationFn: (reviewId) => reviewService.remove(reviewId),
    onSuccess: () => {
      invalidateProductReviews();
      toast.success('Review deleted');
    },
    onError: (error) => toast.error(error.response?.data?.message ?? 'Could not delete review')
  });

  const handleSubmit = (event) => {
    event.preventDefault();
    if (!accessToken) {
      navigate('/login');
      return;
    }
    const payload = {
      productId,
      rating: Number(form.rating),
      title: form.title.trim(),
      comment: form.comment.trim()
    };
    if (!payload.title || !payload.comment) {
      toast.error('Add a title and comment');
      return;
    }
    if (editingReviewId) {
      updateReview.mutate({ reviewId: editingReviewId, payload });
      return;
    }
    createReview.mutate(payload);
  };

  const startEdit = (review) => {
    setEditingReviewId(review.id);
    setForm({ rating: review.rating, title: review.title, comment: review.comment });
  };

  const cancelEdit = () => {
    setEditingReviewId(null);
    setForm(emptyForm);
  };

  const canWriteReview = accessToken && (!currentUserReview || editingReviewId);
  const reviewCount = Number(product.reviewCount ?? reviews.length ?? 0);

  return (
    <section className="mx-auto max-w-7xl px-4 pb-12">
      <div className="grid gap-6 lg:grid-cols-[360px_1fr]">
        <aside className="h-fit rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
          <div className="text-sm font-bold uppercase text-signal">Customer ratings</div>
          <div className="mt-3 flex items-end gap-2">
            <span className="text-4xl font-black">{Number(product.averageRating ?? 0).toFixed(1)}</span>
            <span className="pb-1 text-sm text-black/55 dark:text-white/60">out of 5</span>
          </div>
          <StarRating value={product.averageRating} readOnly className="mt-3" />
          <p className="mt-3 text-sm text-black/60 dark:text-white/65">
            Based on {reviewCount} verified review{reviewCount === 1 ? '' : 's'}.
          </p>
        </aside>

        <div className="grid gap-5">
          <div className="rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
            <h2 className="text-2xl font-black">Reviews</h2>
            {!accessToken && (
              <p className="mt-3 text-sm text-black/60 dark:text-white/65">
                Log in to write a review for this product.
              </p>
            )}
            {accessToken && currentUserReview && !editingReviewId && (
              <p className="mt-3 rounded-md bg-emerald-50 px-3 py-2 text-sm font-semibold text-emerald-700">
                You already reviewed this product. You can edit your review below.
              </p>
            )}
            {canWriteReview && (
              <form onSubmit={handleSubmit} className="mt-5 grid gap-4">
                <div>
                  <div className="mb-2 text-sm font-bold">Your rating</div>
                  <StarRating value={form.rating} onChange={(rating) => setForm((current) => ({ ...current, rating }))} />
                </div>
                <input
                  value={form.title}
                  onChange={(event) => setForm((current) => ({ ...current, title: event.target.value }))}
                  className="rounded-md border border-black/10 px-3 py-2 dark:border-white/10 dark:bg-white/10"
                  maxLength={180}
                  placeholder="Review title"
                />
                <textarea
                  value={form.comment}
                  onChange={(event) => setForm((current) => ({ ...current, comment: event.target.value }))}
                  className="min-h-28 rounded-md border border-black/10 px-3 py-2 dark:border-white/10 dark:bg-white/10"
                  maxLength={4000}
                  placeholder="What should other shoppers know?"
                />
                <div className="flex flex-wrap gap-3">
                  <Button disabled={createReview.isPending || updateReview.isPending}>
                    {editingReviewId ? 'Update review' : 'Submit review'}
                  </Button>
                  {editingReviewId && (
                    <button type="button" onClick={cancelEdit} className="rounded-md px-4 py-2 text-sm font-bold hover:bg-black/5 dark:hover:bg-white/10">
                      Cancel
                    </button>
                  )}
                </div>
              </form>
            )}
          </div>

          <div className="grid gap-3">
            {isLoading && <div className="rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">Loading reviews...</div>}
            {!isLoading && reviews.length === 0 && (
              <div className="rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">No reviews yet. Be the first to rate this product.</div>
            )}
            {reviews.map((review) => {
              const owner = review.userEmail === user?.email;
              const admin = role === 'ROLE_ADMIN';
              return (
                <article key={review.id} className="rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
                  <div className="flex flex-wrap items-start justify-between gap-3">
                    <div>
                      <StarRating value={review.rating} readOnly size={18} />
                      <h3 className="mt-2 text-lg font-black">{review.title}</h3>
                      <p className="mt-1 text-sm text-black/55 dark:text-white/60">
                        {review.userName} | {new Date(review.createdAt).toLocaleDateString()}
                      </p>
                    </div>
                    <div className="flex gap-2">
                      {owner && (
                        <button type="button" onClick={() => startEdit(review)} className="rounded-full p-2 hover:bg-black/5 dark:hover:bg-white/10" aria-label="Edit review">
                          <Edit3 size={17} />
                        </button>
                      )}
                      {(owner || admin) && (
                        <button type="button" onClick={() => deleteReview.mutate(review.id)} className="rounded-full p-2 text-coral hover:bg-black/5 dark:hover:bg-white/10" aria-label="Delete review">
                          <Trash2 size={17} />
                        </button>
                      )}
                    </div>
                  </div>
                  <p className="mt-4 text-black/70 dark:text-white/75">{review.comment}</p>
                </article>
              );
            })}
          </div>
        </div>
      </div>
    </section>
  );
}

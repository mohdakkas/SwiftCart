import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { toast } from 'react-toastify';
import { ImagePlus, Trash2, Upload } from 'lucide-react';
import { Button } from '../../components/Button.jsx';
import { catalogService } from '../../services/catalogService.js';
import { productImageService } from '../../services/productImageService.js';

function ProductImageManager({ product }) {
  const queryClient = useQueryClient();
  const [mainFile, setMainFile] = useState(null);
  const [galleryFiles, setGalleryFiles] = useState([]);
  const mainPreview = useMemo(() => mainFile ? window.URL.createObjectURL(mainFile) : null, [mainFile]);
  const galleryPreviews = useMemo(() => galleryFiles.map((file) => ({ name: file.name, url: window.URL.createObjectURL(file) })), [galleryFiles]);

  const invalidateProducts = () => {
    queryClient.invalidateQueries({ queryKey: ['admin-products'] });
    queryClient.invalidateQueries({ queryKey: ['product', String(product.id)] });
    queryClient.invalidateQueries({ queryKey: ['product', product.id] });
  };

  const uploadMain = useMutation({
    mutationFn: () => productImageService.uploadMain(product.id, mainFile),
    onSuccess: () => {
      setMainFile(null);
      invalidateProducts();
      toast.success('Main image uploaded');
    },
    onError: (error) => toast.error(error.response?.data?.message ?? 'Could not upload main image')
  });

  const uploadGallery = useMutation({
    mutationFn: () => productImageService.uploadGallery(product.id, galleryFiles),
    onSuccess: () => {
      setGalleryFiles([]);
      invalidateProducts();
      toast.success('Gallery images uploaded');
    },
    onError: (error) => toast.error(error.response?.data?.message ?? 'Could not upload gallery images')
  });

  const deleteImage = useMutation({
    mutationFn: (imageId) => productImageService.remove(imageId),
    onSuccess: () => {
      invalidateProducts();
      toast.success('Image deleted');
    },
    onError: (error) => toast.error(error.response?.data?.message ?? 'Could not delete image')
  });

  return (
    <article className="rounded-lg border border-black/10 bg-white p-5 shadow-sm">
      <div className="grid gap-5 lg:grid-cols-[180px_1fr]">
        <img
          src={product.imageUrl || product.images?.[0] || 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3'}
          alt={product.name}
          className="aspect-square w-full rounded-md object-cover"
        />
        <div className="min-w-0">
          <div className="flex flex-wrap items-start justify-between gap-3">
            <div>
              <h2 className="text-xl font-black">{product.name}</h2>
              <p className="text-sm text-black/55">{product.category}</p>
            </div>
            <span className="rounded-full bg-signal/10 px-3 py-1 text-xs font-black text-signal">{product.productImages?.length ?? product.images?.length ?? 0} images</span>
          </div>

          <div className="mt-5 grid gap-4 md:grid-cols-2">
            <div className="rounded-md border border-black/10 p-4">
              <div className="mb-3 flex items-center gap-2 font-black"><ImagePlus size={18} /> Main image</div>
              <input type="file" accept="image/jpeg,image/png,image/webp" onChange={(event) => setMainFile(event.target.files?.[0] ?? null)} />
              {mainPreview && <img src={mainPreview} alt="Main preview" className="mt-3 h-24 w-24 rounded-md object-cover" />}
              <Button className="mt-3" disabled={!mainFile || uploadMain.isPending} onClick={() => uploadMain.mutate()}><Upload size={16} /> Upload main</Button>
            </div>

            <div className="rounded-md border border-black/10 p-4">
              <div className="mb-3 flex items-center gap-2 font-black"><ImagePlus size={18} /> Gallery images</div>
              <input type="file" multiple accept="image/jpeg,image/png,image/webp" onChange={(event) => setGalleryFiles(Array.from(event.target.files ?? []))} />
              {galleryPreviews.length > 0 && (
                <div className="mt-3 flex flex-wrap gap-2">
                  {galleryPreviews.map((preview) => <img key={preview.name} src={preview.url} alt={preview.name} className="h-16 w-16 rounded-md object-cover" />)}
                </div>
              )}
              <Button className="mt-3" disabled={galleryFiles.length === 0 || uploadGallery.isPending} onClick={() => uploadGallery.mutate()}><Upload size={16} /> Upload gallery</Button>
            </div>
          </div>

          <div className="mt-5 flex flex-wrap gap-3">
            {(product.productImages ?? []).map((image) => (
              <div key={image.id} className="group relative">
                <img src={image.imageUrl} alt={product.name} className="h-20 w-20 rounded-md border border-black/10 object-cover" />
                {image.mainImage && <span className="absolute left-1 top-1 rounded bg-signal px-1.5 py-0.5 text-[10px] font-black text-white">MAIN</span>}
                <button
                  type="button"
                  aria-label="Delete image"
                  onClick={() => deleteImage.mutate(image.id)}
                  className="absolute -right-2 -top-2 rounded-full bg-coral p-1 text-white opacity-0 shadow group-hover:opacity-100"
                >
                  <Trash2 size={14} />
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>
    </article>
  );
}

export function AdminProductsPage() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['admin-products'],
    queryFn: () => catalogService.products({ size: 50 }),
    retry: false
  });
  const products = data?.content ?? [];

  return (
    <section className="p-6">
      <h1 className="text-3xl font-black">Product images</h1>
      <p className="mt-1 text-sm text-black/55">Upload main product images and gallery photos from your computer.</p>
      {isLoading && <div className="mt-6 h-40 animate-pulse rounded-lg bg-white shadow-sm" />}
      {isError && <div className="mt-6 rounded-lg bg-red-50 p-4 font-semibold text-red-700">Could not load products.</div>}
      <div className="mt-6 grid gap-5">
        {products.map((product) => <ProductImageManager key={product.id} product={product} />)}
      </div>
    </section>
  );
}

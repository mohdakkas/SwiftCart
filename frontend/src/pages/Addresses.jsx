import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { toast } from 'react-toastify';
import { CheckCircle2, Home, Pencil, Plus, Trash2 } from 'lucide-react';
import { Button } from '../components/Button.jsx';
import { addressService } from '../services/addressService.js';

const emptyAddress = {
  fullName: '',
  phoneNumber: '',
  addressLine1: '',
  addressLine2: '',
  city: '',
  state: '',
  postalCode: '',
  country: 'India',
  addressType: 'HOME',
  defaultAddress: false
};

export function AddressForm({ initialValue = emptyAddress, onSubmit, onCancel, isSaving }) {
  const [form, setForm] = useState(initialValue);
  const update = (key, value) => setForm((current) => ({ ...current, [key]: value }));

  return (
    <form onSubmit={(event) => { event.preventDefault(); onSubmit(form); }} className="grid gap-3 rounded-lg border border-black/10 bg-white p-5 shadow-sm dark:border-white/10 dark:bg-white/5">
      <div className="grid gap-3 md:grid-cols-2">
        <input required value={form.fullName} onChange={(e) => update('fullName', e.target.value)} className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="Full name" />
        <input required pattern="[6-9][0-9]{9}" value={form.phoneNumber} onChange={(e) => update('phoneNumber', e.target.value)} className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="10-digit phone number" />
      </div>
      <input required value={form.addressLine1} onChange={(e) => update('addressLine1', e.target.value)} className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="Address line 1" />
      <input value={form.addressLine2 ?? ''} onChange={(e) => update('addressLine2', e.target.value)} className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="Address line 2" />
      <div className="grid gap-3 md:grid-cols-3">
        <input required value={form.city} onChange={(e) => update('city', e.target.value)} className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="City" />
        <input required value={form.state} onChange={(e) => update('state', e.target.value)} className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="State" />
        <input required pattern="[0-9]{5,6}" value={form.postalCode} onChange={(e) => update('postalCode', e.target.value)} className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="Postal code" />
      </div>
      <div className="grid gap-3 md:grid-cols-2">
        <input required value={form.country} onChange={(e) => update('country', e.target.value)} className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="Country" />
        <select value={form.addressType} onChange={(e) => update('addressType', e.target.value)} className="rounded-md border px-3 py-2 dark:bg-white/10">
          <option value="HOME">Home</option>
          <option value="OFFICE">Office</option>
          <option value="OTHER">Other</option>
        </select>
      </div>
      <label className="flex items-center gap-2 text-sm font-semibold"><input type="checkbox" checked={Boolean(form.defaultAddress)} onChange={(e) => update('defaultAddress', e.target.checked)} /> Set as default address</label>
      <div className="flex flex-wrap gap-3">
        <Button disabled={isSaving}>{isSaving ? 'Saving...' : 'Save address'}</Button>
        {onCancel && <button type="button" onClick={onCancel} className="rounded-md px-4 py-2 text-sm font-bold hover:bg-black/5 dark:hover:bg-white/10">Cancel</button>}
      </div>
    </form>
  );
}

export function AddressCard({ address, onEdit, onDelete, onDefault, showActions = true }) {
  return (
    <article className={`rounded-lg border bg-white p-5 shadow-sm dark:bg-white/5 ${address.defaultAddress ? 'border-signal ring-2 ring-signal/15' : 'border-black/10 dark:border-white/10'}`}>
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <div className="flex flex-wrap items-center gap-2">
            <span className="inline-flex items-center gap-1 rounded-full bg-signal/10 px-2 py-1 text-xs font-black text-signal"><Home size={13} /> {address.addressType}</span>
            {address.defaultAddress && <span className="inline-flex items-center gap-1 rounded-full bg-emerald-50 px-2 py-1 text-xs font-black text-emerald-700"><CheckCircle2 size={13} /> DEFAULT</span>}
          </div>
          <h2 className="mt-3 text-lg font-black">{address.fullName}</h2>
          <p className="mt-1 text-sm font-semibold text-black/60 dark:text-white/65">{address.phoneNumber}</p>
          <p className="mt-3 text-sm text-black/70 dark:text-white/75">{address.addressLine1}{address.addressLine2 ? `, ${address.addressLine2}` : ''}</p>
          <p className="text-sm text-black/70 dark:text-white/75">{address.city}, {address.state} {address.postalCode}, {address.country}</p>
        </div>
        <div className="flex gap-2">
          <button type="button" onClick={() => onEdit(address)} className="rounded-full p-2 hover:bg-black/5 dark:hover:bg-white/10" aria-label="Edit address"><Pencil size={17} /></button>
          <button type="button" onClick={() => onDelete(address.id)} className="rounded-full p-2 text-coral hover:bg-black/5 dark:hover:bg-white/10" aria-label="Delete address"><Trash2 size={17} /></button>
        </div>
      </div>
      {showActions && !address.defaultAddress && <button type="button" onClick={() => onDefault(address.id)} className="mt-4 text-sm font-bold text-signal">Set as default</button>}
    </article>
  );
}

export function Addresses() {
  const queryClient = useQueryClient();
  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState(null);
  const { data: addresses = [], isLoading } = useQuery({ queryKey: ['addresses'], queryFn: addressService.list, retry: false });
  const refresh = () => queryClient.invalidateQueries({ queryKey: ['addresses'] });

  const createAddress = useMutation({ mutationFn: addressService.create, onSuccess: () => { toast.success('Address added'); setShowForm(false); refresh(); }, onError: (e) => toast.error(e.response?.data?.message ?? 'Could not save address') });
  const updateAddress = useMutation({ mutationFn: ({ id, payload }) => addressService.update(id, payload), onSuccess: () => { toast.success('Address updated'); setEditing(null); refresh(); }, onError: (e) => toast.error(e.response?.data?.message ?? 'Could not update address') });
  const deleteAddress = useMutation({ mutationFn: addressService.remove, onSuccess: () => { toast.success('Address deleted'); refresh(); }, onError: (e) => toast.error(e.response?.data?.message ?? 'Could not delete address') });
  const setDefault = useMutation({ mutationFn: addressService.setDefault, onSuccess: () => { toast.success('Default address updated'); refresh(); }, onError: (e) => toast.error(e.response?.data?.message ?? 'Could not update default address') });

  return (
    <section className="mx-auto max-w-5xl px-4 py-10">
      <div className="mb-6 flex items-center justify-between gap-4">
        <h1 className="text-3xl font-black">My Addresses</h1>
        <Button onClick={() => { setEditing(null); setShowForm(true); }}><Plus size={16} /> Add address</Button>
      </div>
      {showForm && <div className="mb-6"><AddressForm onSubmit={(payload) => createAddress.mutate(payload)} onCancel={() => setShowForm(false)} isSaving={createAddress.isPending} /></div>}
      {editing && <div className="mb-6"><AddressForm initialValue={editing} onSubmit={(payload) => updateAddress.mutate({ id: editing.id, payload })} onCancel={() => setEditing(null)} isSaving={updateAddress.isPending} /></div>}
      {isLoading && <div className="rounded-lg bg-white p-6 dark:bg-white/5">Loading addresses...</div>}
      {!isLoading && addresses.length === 0 && <div className="rounded-lg bg-white p-6 dark:bg-white/5">No saved addresses yet.</div>}
      <div className="grid gap-4 md:grid-cols-2">
        {addresses.map((address) => <AddressCard key={address.id} address={address} onEdit={setEditing} onDelete={(id) => deleteAddress.mutate(id)} onDefault={(id) => setDefault.mutate(id)} />)}
      </div>
    </section>
  );
}


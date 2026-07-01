import { useEffect, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { CreditCard, MapPin, Plus } from 'lucide-react';
import { Button } from '../components/Button.jsx';
import { AddressCard, AddressForm } from './Addresses.jsx';
import { addressService } from '../services/addressService.js';
import { cartService } from '../services/cartService.js';
import { orderService } from '../services/orderService.js';
import { paymentService } from '../services/paymentService.js';

const paymentOptions = [
  { label: 'Cash on Delivery', value: 'CASH_ON_DELIVERY', helper: 'Pay when your order arrives.' },
  { label: 'UPI', value: 'UPI', helper: 'Pay securely with Razorpay UPI in test mode.' },
  { label: 'Card Payment', value: 'CREDIT_CARD', helper: 'Pay securely with Razorpay card checkout.' }
];

function loadRazorpayCheckout() {
  if (window.Razorpay) {
    return Promise.resolve(true);
  }
  return new Promise((resolve, reject) => {
    const existingScript = document.querySelector('script[src="https://checkout.razorpay.com/v1/checkout.js"]');
    if (existingScript) {
      existingScript.addEventListener('load', () => resolve(true));
      existingScript.addEventListener('error', () => reject(new Error('Could not load Razorpay Checkout')));
      return;
    }
    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;
    script.onload = () => resolve(true);
    script.onerror = () => reject(new Error('Could not load Razorpay Checkout'));
    document.body.appendChild(script);
  });
}

export function Checkout() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [paymentMethod, setPaymentMethod] = useState('CASH_ON_DELIVERY');
  const [selectedAddressId, setSelectedAddressId] = useState(null);
  const [showAddressForm, setShowAddressForm] = useState(false);
  const [paymentResult, setPaymentResult] = useState(null);
  const [successOrder, setSuccessOrder] = useState(null);
  const { data: cart } = useQuery({ queryKey: ['cart'], queryFn: cartService.get, retry: false });
  const { data: addresses = [] } = useQuery({ queryKey: ['addresses'], queryFn: addressService.list, retry: false });
  const selectedPayment = paymentOptions.find((method) => method.value === paymentMethod);
  const selectedAddress = useMemo(() => addresses.find((address) => address.id === selectedAddressId) ?? addresses.find((address) => address.defaultAddress) ?? addresses[0], [addresses, selectedAddressId]);

  useEffect(() => {
    if (!selectedAddressId && selectedAddress) {
      setSelectedAddressId(selectedAddress.id);
    }
  }, [selectedAddress, selectedAddressId]);

  const createAddress = useMutation({
    mutationFn: addressService.create,
    onSuccess: (address) => {
      toast.success('Address added');
      setShowAddressForm(false);
      setSelectedAddressId(address.id);
      queryClient.invalidateQueries({ queryKey: ['addresses'] });
    },
    onError: (error) => toast.error(error.response?.data?.message ?? 'Could not add address')
  });

  const invalidateCheckoutData = () => {
    queryClient.invalidateQueries({ queryKey: ['cart'] });
    queryClient.invalidateQueries({ queryKey: ['orders'] });
    queryClient.invalidateQueries({ queryKey: ['products'] });
  };

  const openRazorpayCheckout = async () => {
    const razorpayOrder = await paymentService.createRazorpayOrder({
      shippingAddressId: selectedAddress.id,
      billingAddressId: selectedAddress.id,
      paymentMethod
    });
    await loadRazorpayCheckout();

    return new Promise((resolve, reject) => {
      const checkout = new window.Razorpay({
        key: razorpayOrder.keyId,
        amount: razorpayOrder.amountInPaise,
        currency: razorpayOrder.currency,
        name: 'SwiftCart',
        description: `Order ${razorpayOrder.swiftCartOrderNumber}`,
        order_id: razorpayOrder.razorpayOrderId,
        prefill: {
          name: razorpayOrder.customerName,
          email: razorpayOrder.customerEmail,
          contact: razorpayOrder.customerPhone
        },
        method: {
          upi: paymentMethod === 'UPI',
          card: paymentMethod === 'CREDIT_CARD',
          netbanking: false,
          wallet: false
        },
        theme: { color: '#0f766e' },
        handler: async (response) => {
          try {
            const order = await paymentService.verifyRazorpayPayment({
              swiftCartOrderNumber: razorpayOrder.swiftCartOrderNumber,
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature
            });
            setPaymentResult({ status: 'SUCCESS', method: paymentMethod, transactionId: response.razorpay_payment_id });
            resolve(order);
          } catch (error) {
            reject(error);
          }
        },
        modal: {
          ondismiss: async () => {
            try {
              await paymentService.markRazorpayFailed({ swiftCartOrderNumber: razorpayOrder.swiftCartOrderNumber, reason: 'Customer closed Razorpay Checkout' });
            } finally {
              reject(new Error('Payment cancelled'));
            }
          }
        }
      });

      if (typeof checkout.on === 'function') {
        checkout.on('payment.failed', async (response) => {
          const reason = response?.error?.description ?? 'Razorpay payment failed';
          try {
            await paymentService.markRazorpayFailed({ swiftCartOrderNumber: razorpayOrder.swiftCartOrderNumber, reason });
          } finally {
            reject(new Error(reason));
          }
        });
      }

      checkout.open();
    });
  };

  const placeOrder = useMutation({
    mutationFn: async () => {
      if (paymentMethod === 'CASH_ON_DELIVERY') {
        return orderService.place({
          shippingAddressId: selectedAddress.id,
          billingAddressId: selectedAddress.id,
          paymentMethod
        });
      }
      return openRazorpayCheckout();
    },
    onSuccess: (order) => {
      setSuccessOrder(order);
      invalidateCheckoutData();
      toast.success(paymentMethod === 'CASH_ON_DELIVERY' ? 'Order placed with Cash on Delivery' : 'Payment successful and order placed');
      navigate('/payment-success', { state: { order, paymentMethod } });
    },
    onError: (error) => {
      const message = error.response?.data?.message ?? error.message ?? 'Could not place order';
      toast.error(message);
      if (paymentMethod !== 'CASH_ON_DELIVERY') {
        navigate('/payment-failed', { state: { message } });
      }
    }
  });

  const canPlaceOrder = selectedAddress && cart?.items?.length > 0;

  return (
    <section className="mx-auto max-w-6xl px-4 py-10">
      <h1 className="text-3xl font-black">Checkout</h1>
      {successOrder && <div className="mt-5 rounded-lg bg-emerald-50 p-4 font-semibold text-emerald-700">Order {successOrder.orderNumber} placed successfully.</div>}
      {paymentResult && !successOrder && <div className="mt-5 rounded-lg bg-signal/10 p-4 font-semibold text-signal">Payment {paymentResult.status}: {paymentResult.method} transaction {paymentResult.transactionId}</div>}

      <div className="mt-6 grid gap-5 lg:grid-cols-[1fr_360px]">
        <div className="grid gap-5">
          <section className="rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
            <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
              <div className="flex items-center gap-2"><MapPin className="text-signal" /><h2 className="text-xl font-black">Select shipping address</h2></div>
              <Button onClick={() => setShowAddressForm((value) => !value)}><Plus size={16} /> New address</Button>
            </div>
            {showAddressForm && <div className="mb-5"><AddressForm onSubmit={(payload) => createAddress.mutate(payload)} onCancel={() => setShowAddressForm(false)} isSaving={createAddress.isPending} /></div>}
            {addresses.length === 0 && !showAddressForm && <p className="text-sm font-semibold text-coral">Add an address before placing your order.</p>}
            <div className="grid gap-3 md:grid-cols-2">
              {addresses.map((address) => (
                <button key={address.id} type="button" onClick={() => setSelectedAddressId(address.id)} className="text-left">
                  <div className={selectedAddress?.id === address.id ? 'rounded-lg ring-2 ring-signal' : 'rounded-lg'}>
                    <AddressCard address={address} showActions={false} />
                  </div>
                </button>
              ))}
            </div>
          </section>

          <section className="rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
            <div className="mb-4 flex items-center gap-2"><CreditCard className="text-signal" /><h2 className="text-xl font-black">Payment method</h2></div>
            <div className="grid gap-3 text-sm md:grid-cols-3">
              {paymentOptions.map((method) => (
                <label key={method.value} className={`rounded-md border p-3 ${paymentMethod === method.value ? 'border-signal bg-signal/5' : 'border-black/10 dark:border-white/10'}`}>
                  <span className="flex gap-2 font-bold"><input name="payment" type="radio" checked={paymentMethod === method.value} onChange={() => { setPaymentMethod(method.value); setPaymentResult(null); }} /> {method.label}</span>
                  <span className="mt-1 block text-xs text-black/55 dark:text-white/60">{method.helper}</span>
                </label>
              ))}
            </div>
          </section>
        </div>

        <aside className="h-fit rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
          <h2 className="text-xl font-black">Order summary</h2>
          {['subtotal', 'tax', 'shipping', 'total'].map((key) => <div key={key} className="mt-3 flex justify-between capitalize"><span>{key}</span><strong>Rs. {Number(cart?.[key] ?? 0).toLocaleString('en-IN')}</strong></div>)}
          <div className="mt-4 rounded-md bg-black/5 p-3 text-sm font-semibold dark:bg-white/10">Selected payment: {selectedPayment?.label}</div>
          <Button className="mt-5 w-full" disabled={!canPlaceOrder || placeOrder.isPending} onClick={() => placeOrder.mutate()}>{placeOrder.isPending ? 'Processing payment...' : paymentMethod === 'CASH_ON_DELIVERY' ? 'Place order' : 'Pay with Razorpay'}</Button>
        </aside>
      </div>
    </section>
  );
}

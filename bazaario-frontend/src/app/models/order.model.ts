export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
export type PaymentMethod = 'UPI' | 'CARD' | 'NET_BANKING' | 'COD';
export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED';

export interface OrderItem {
  orderItemId: number;
  productId: number;
  productName: string;
  imageUrl: string | null;
  quantity: number;
  price: number;
  subtotal: number;
}

export interface Order {
  orderId: number;
  userId: number;
  customerName: string;
  orderDate: string;
  totalAmount: number;
  status: OrderStatus;
  shippingAddress: string;
  items: OrderItem[];
}

export interface Payment {
  paymentId: number;
  orderId: number;
  paymentMethod: PaymentMethod;
  paymentStatus: PaymentStatus;
  paymentDate: string;
  amount: number;
  transactionId: string | null;
}

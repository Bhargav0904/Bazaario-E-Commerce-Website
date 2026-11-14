export interface CartItem {
  cartItemId: number;
  productId: number;
  productName: string;
  imageUrl: string | null;
  price: number;
  quantity: number;
  availableStock: number;
  subtotal: number;
}

export interface Cart {
  cartId: number | null;
  items: CartItem[];
  total: number;
  itemCount: number;
}

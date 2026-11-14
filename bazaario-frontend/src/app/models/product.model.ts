export interface Product {
  productId: number;
  productName: string;
  description: string;
  price: number;
  quantity: number;
  categoryId: number;
  categoryName: string;
  imageUrl: string | null;
}

export interface ProductRequest {
  productName: string;
  description: string;
  price: number;
  quantity: number;
  categoryId: number;
  imageUrl?: string;
}

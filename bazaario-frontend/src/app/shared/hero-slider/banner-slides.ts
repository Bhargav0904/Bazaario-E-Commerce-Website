// ============================================================================
//  EDIT THIS FILE TO CHANGE THE HOME PAGE SLIDER
//
//  1. Put your banner pictures in the folder:   public/banners/
//  2. Add one block { ... } for each picture below (copy an existing block).
//  3. Save. The slider updates by itself while `ng serve` is running.
//
//  Picture tips: size 1600 x 600 pixels (wide), JPG or WebP, under 300 KB each.
//  Keep the important part of the picture in the middle, because small screens
//  crop the left and right edges.
// ============================================================================

export interface BannerSlide {
  /** Picture file inside public/banners, e.g. 'banners/sale.jpg' (a full https:// link also works) */
  image: string;
  /** Short description of the picture (read by screen readers, shown if the picture fails) */
  alt: string;
  /** Big text on the banner (optional). Write \n to start a new line */
  title?: string;
  /** Small text under the title (optional) */
  text?: string;
  /** Button label, e.g. 'Shop Now' (optional). Leave it out for a picture-only banner */
  buttonText?: string;
  /** Where a click goes: '/products', '/products?categoryId=2', '/products?keyword=phone' ... (optional) */
  link?: string;
}

export const BANNER_SLIDES: BannerSlide[] = [
  {
    image: 'banners/banner-1.jpg',
    alt: 'Big savings on everyday products',
    title: 'Upgrade\nYour Everyday',
    text: 'Great products. Fair prices.',
    buttonText: 'Shop Now',
    link: '/products',
  },
  {
    image: 'banners/banner-2.jpg',
    alt: 'New arrivals in the store',
    title: 'Fresh Picks\nJust Added',
    text: 'See what is new this week.',
    buttonText: 'Explore',
    link: '/products',
  },
  {
    image: 'banners/banner-3.jpg',
    alt: 'Picture-only banner: tap to open the shop',
    link: '/products',
  },
];

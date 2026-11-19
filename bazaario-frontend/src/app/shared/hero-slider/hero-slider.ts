import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';

import { BANNER_SLIDES, BannerSlide } from './banner-slides';

const AUTOPLAY_MS = 5000; // how long each slide stays (milliseconds)
const SWIPE_PX = 50;      // how far to drag before it counts as a swipe

// The home page slider. The pictures and texts come from banner-slides.ts.
@Component({
  selector: 'app-hero-slider',
  templateUrl: './hero-slider.html',
  styleUrl: './hero-slider.css',
})
export class HeroSlider implements OnInit, OnDestroy {
  readonly slides: BannerSlide[] = BANNER_SLIDES;

  index = signal(0);                                    // slide that is showing
  hovering = signal(false);                             // mouse is over the slider
  stopped = signal(false);                              // visitor pressed the pause button
  broken = signal<ReadonlySet<number>>(new Set());      // pictures that could not be loaded

  private timer: ReturnType<typeof setInterval> | null = null;
  private startX = 0;
  private swiped = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.restartTimer();
  }

  ngOnDestroy(): void {
    this.clearTimer();
  }

  // ---------- moving between slides ----------
  next(): void {
    this.goTo(this.index() + 1);
  }

  prev(): void {
    this.goTo(this.index() - 1);
  }

  goTo(i: number): void {
    const n = this.slides.length;
    if (n === 0) return;
    this.index.set(((i % n) + n) % n); // wraps around: after the last slide comes the first
    this.restartTimer();               // the new slide gets its full time
  }

  toggle(): void {
    this.stopped.set(!this.stopped());
  }

  markBroken(i: number): void {
    this.broken.update((set) => new Set(set).add(i));
  }

  // ---------- keyboard and swipe ----------
  onKey(event: KeyboardEvent): void {
    if (event.key === 'ArrowLeft') this.prev();
    if (event.key === 'ArrowRight') this.next();
  }

  onPointerDown(event: PointerEvent): void {
    this.startX = event.clientX;
    this.swiped = false;
  }

  onPointerUp(event: PointerEvent): void {
    const dx = event.clientX - this.startX;
    if (Math.abs(dx) > SWIPE_PX) {
      this.swiped = true; // so the swipe is not also treated as a click
      if (dx < 0) this.next();
      else this.prev();
    }
  }

  // ---------- clicks ----------
  onSlideClick(slide: BannerSlide): void {
    // slides with a button use the button; picture-only slides are clickable as a whole
    if (slide.buttonText) return;
    this.open(slide.link);
  }

  open(link?: string): void {
    if (this.swiped || !link) return;
    if (/^https?:\/\//i.test(link)) {
      window.location.href = link;
    } else {
      this.router.navigateByUrl(link);
    }
  }

  // ---------- auto-play ----------
  private restartTimer(): void {
    this.clearTimer();
    if (this.slides.length < 2) return;
    this.timer = setInterval(() => {
      // skip while the mouse is over the slider, after pause, or while the tab is hidden
      if (this.hovering() || this.stopped() || document.hidden) return;
      this.index.set((this.index() + 1) % this.slides.length);
    }, AUTOPLAY_MS);
  }

  private clearTimer(): void {
    if (this.timer) clearInterval(this.timer);
    this.timer = null;
  }
}

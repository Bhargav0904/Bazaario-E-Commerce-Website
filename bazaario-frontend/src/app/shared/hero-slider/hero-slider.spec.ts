import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { vi } from 'vitest';

import { HeroSlider } from './hero-slider';
import { BANNER_SLIDES } from './banner-slides';

describe('HeroSlider', () => {
  async function create() {
    await TestBed.configureTestingModule({
      imports: [HeroSlider],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(HeroSlider);
    fixture.detectChanges();
    return fixture;
  }

  it('shows one slide for every entry in banner-slides.ts', async () => {
    const el = (await create()).nativeElement as HTMLElement;
    expect(el.querySelectorAll('.slide').length).toBe(BANNER_SLIDES.length);
  });

  it('wraps around at both ends', async () => {
    const slider = (await create()).componentInstance;
    const n = BANNER_SLIDES.length;
    slider.goTo(-1);
    expect(slider.index()).toBe(n - 1);
    slider.goTo(n);
    expect(slider.index()).toBe(0);
  });

  it('moves to the next slide when the visitor swipes left', async () => {
    const slider = (await create()).componentInstance;
    if (BANNER_SLIDES.length < 2) return;
    slider.onPointerDown({ clientX: 300 } as PointerEvent);
    slider.onPointerUp({ clientX: 100 } as PointerEvent);
    expect(slider.index()).toBe(1);
  });

  it('opens a link inside the app', async () => {
    const fixture = await create();
    const nav = vi.spyOn(TestBed.inject(Router), 'navigateByUrl').mockResolvedValue(true);
    fixture.componentInstance.open('/products?categoryId=2');
    expect(nav).toHaveBeenCalledWith('/products?categoryId=2');
  });
});

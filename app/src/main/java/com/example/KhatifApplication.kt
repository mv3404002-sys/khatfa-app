package com.example

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy

class KhatifApplication : Application(), ImageLoaderFactory {
  override fun newImageLoader(): ImageLoader {
    return ImageLoader.Builder(this)
      .memoryCache {
        MemoryCache.Builder(this)
          .maxSizePercent(0.25)
          .build()
      }
      .diskCache {
        DiskCache.Builder()
          .directory(cacheDir.resolve("image_cache"))
          .maxSizeBytes(100L * 1024 * 1024) // 100 MB real disk cache
          .build()
      }
      .diskCachePolicy(CachePolicy.ENABLED)
      .memoryCachePolicy(CachePolicy.ENABLED)
      .networkCachePolicy(CachePolicy.ENABLED)
      .respectCacheHeaders(false) // Cache video thumbnails without re-requesting from network
      .crossfade(true)
      .build()
  }
}

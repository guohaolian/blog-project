import { ref } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * Tiny helper to run an async request with:
 * - loading ref
 * - default error toast (semantic, user-facing)
 * - simple toast throttling to avoid spam on concurrent requests
 */

let lastToastAt = 0
function toastOnce(message: string, intervalMs = 1500) {
  const now = Date.now()
  if (now - lastToastAt < intervalMs) return
  lastToastAt = now
  ElMessage.error(message)
}

export function useAsyncTask<TArgs extends any[], TResult>(
  task: (...args: TArgs) => Promise<TResult>,
  options?: {
    /** fallback message when error has no message */
    defaultErrorMessage?: string
    /** toast interval ms */
    toastIntervalMs?: number
    /** if true, do not toast; just rethrow */
    silent?: boolean
  },
) {
  const loading = ref(false)

  const run = async (...args: TArgs): Promise<TResult | null> => {
    loading.value = true
    try {
      return await task(...args)
    } catch (e: any) {
      if (!options?.silent) {
        toastOnce(
          e?.message || options?.defaultErrorMessage || 'Request failed',
          options?.toastIntervalMs ?? 1500,
        )
      }
      return null
    } finally {
      loading.value = false
    }
  }

  return { loading, run }
}

/**
 * Convenience wrapper for one-off requests without creating a composable.
 */
export async function runWithErrorToast<TResult>(
  task: () => Promise<TResult>,
  options?: {
    defaultErrorMessage?: string
    toastIntervalMs?: number
    silent?: boolean
  },
): Promise<TResult | null> {
  try {
    return await task()
  } catch (e: any) {
    if (!options?.silent) {
      toastOnce(
        e?.message || options?.defaultErrorMessage || 'Request failed',
        options?.toastIntervalMs ?? 1500,
      )
    }
    return null
  }
}


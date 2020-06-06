package de.rki.coronawarnapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import de.rki.coronawarnapp.BuildConfig
import timber.log.Timber

/**
 * Periodic diagnosis key retrieval work
 * Executes the scheduling of one time diagnosis key retrieval work
 *
 * @see BackgroundWorkScheduler
 * @see DiagnosisKeyRetrievalOneTimeWorker
 */
class DiagnosisKeyRetrievalPeriodicWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    /**
     * Work execution
     *
     * @return Result
     *
     * @see BackgroundWorkScheduler.scheduleDiagnosisKeyPeriodicWork()
     * @see BackgroundWorkScheduler.scheduleDiagnosisKeyOneTimeWork()
     */
    override suspend fun doWork(): Result {
        if (BuildConfig.DEBUG) Timber.d("Background job started. Run attempt: %i", runAttemptCount)

        if (runAttemptCount > BackgroundConstants.WORKER_RETRY_COUNT_THRESHOLD) {
            if (BuildConfig.DEBUG) Timber.d(
                "Background job failed after %i attempts. Rescheduling",
                runAttemptCount
            )
            BackgroundWorkScheduler.scheduleDiagnosisKeyPeriodicWork()
            return Result.failure()
        }
        var result = Result.success()
        try {
            BackgroundWorkScheduler.scheduleDiagnosisKeyOneTimeWork()
        } catch (e: Exception) {
            result = Result.retry()
        }
        return result
    }
}

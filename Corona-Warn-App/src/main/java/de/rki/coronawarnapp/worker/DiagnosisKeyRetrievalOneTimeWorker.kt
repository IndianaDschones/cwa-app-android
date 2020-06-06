package de.rki.coronawarnapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import de.rki.coronawarnapp.BuildConfig
import de.rki.coronawarnapp.transaction.RetrieveDiagnosisKeysTransaction
import timber.log.Timber

/**
 * One time diagnosis key retrieval work
 * Executes the retrieve diagnosis key transaction
 *
 * @see BackgroundWorkScheduler
 */
class DiagnosisKeyRetrievalOneTimeWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    /**
     * Work execution
     *
     * @return Result
     *
     * @see RetrieveDiagnosisKeysTransaction
     */
    override suspend fun doWork(): Result {
        if (BuildConfig.DEBUG) Timber.d("Background job started. Run attempt: %i", runAttemptCount)

        if (runAttemptCount > BackgroundConstants.WORKER_RETRY_COUNT_THRESHOLD) {
            if (BuildConfig.DEBUG) Timber.d(
                "Background job failed after %i attempts. Rescheduling",
                runAttemptCount
            )
            return Result.failure()
        }
        var result = Result.success()
        try {
            RetrieveDiagnosisKeysTransaction.start()
        } catch (e: Exception) {
            result = Result.retry()
        }
        return result
    }
}

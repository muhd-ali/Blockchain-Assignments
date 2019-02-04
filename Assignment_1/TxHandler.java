public class TxHandler {

    UTXOPool uPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        uPool = new UTXOPool(utxoPool);
    }

    private boolean areOutputsInUTXOPool(Transaction tx) {
        return false;
    }

    private boolean areInputSignaturesValid(Transaction tx) {
        return false;
    }

    private boolean isAnyUTXOClaimedMultipleTimes(Transaction tx) {
        return false;
    }

    private boolean areAllOutputValuesNonNegative(Transaction tx) {
        return false;
    }

    private boolean isTotalInputGreaterThanOrEqualToTotalOutput(Transaction tx) {
        return false;
    }

    /**
     * @return true if:
     *          (1) all outputs claimed by {@code tx} are in the current UTXO pool ({@code areOutputsInUTXOPool}),
     *          (2) the signatures on each input of {@code tx} are valid ({@code areInputSignaturesValid}),
     *          (3) no UTXO is claimed multiple times by {@code tx} ({@code isAnyUTXOClaimedMultipleTimes}),
     *          (4) all of {@code tx}s output values are non-negative ({@code areAllOutputValuesNonNegative}), and
     *          (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output values ({@code isTotalInputGreaterThanOrEqualToTotalOutput});
     *          and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        return (
            areOutputsInUTXOPool(tx) &&
            areInputSignaturesValid(tx) &&
            !isAnyUTXOClaimedMultipleTimes(tx) &&
            areAllOutputValuesNonNegative(tx) &&
            isTotalInputGreaterThanOrEqualToTotalOutput(tx)
        );
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        return null;
    }

}

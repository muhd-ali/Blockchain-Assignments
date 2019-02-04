import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TxHandler {

    UTXOPool uPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent
     * transaction outputs) is {@code utxoPool}. This should make a copy of utxoPool
     * by using the UTXOPool(UTXOPool uPool) constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        uPool = new UTXOPool(utxoPool);
    }

    private boolean existsInUTXOPool(UTXO utxo) {
        return uPool.contains(utxo);
    }

    private boolean isInputSignatureValid(PublicKey publicKey, byte[] rawData, byte[] signature) {
		return Crypto.verifySignature(publicKey, rawData, signature);
	}

    private boolean isUTXOClaimedMultipleTimes(Set<UTXO> claimedUTXOs, UTXO utxo) {
        return !claimedUTXOs.add(utxo);
    }

    /**
     * @return true if: (1) all outputs claimed by {@code tx} are in the current
     *         UTXO pool ({@code existsInUTXOPool}), (2) the signatures on each
     *         input of {@code tx} are valid ({@code isInputSignatureValid}), (3)
     *         no UTXO is claimed multiple times by {@code tx}
     *         ({@code isUTXOClaimedMultipleTimes}), (4) all of {@code tx}s
     *         output values are non-negative
     *         ({@code areAllOutputValuesNonNegative}), and (5) the sum of
     *         {@code tx}s input values is greater than or equal to the sum of its
     *         output values ({@code isTotalInputGreaterThanOrEqualToTotalOutput});
     *         and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        Set<UTXO> claimedUTXOs = new HashSet<UTXO>();
        double totalInput = 0;

        int index = 0;
        for (Transaction.Input input : tx.getInputs()) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output correspondingOutput = uPool.getTxOutput(utxo);

            if (!existsInUTXOPool(utxo)) {
                return false;
            }

            if (!isInputSignatureValid(
                correspondingOutput.address,
                tx.getRawDataToSign(index),
                input.signature
            )) {
                return false;
            }

            if (isUTXOClaimedMultipleTimes(claimedUTXOs, utxo)) {
                return false;
            }


            totalInput += correspondingOutput.value;
            index++;
        }

        double totalOutput = 0;

        for (Transaction.Output output : tx.getOutputs()) {
            if (output.value < 0) {
                return false;
            }
            totalOutput += output.value;
        }

        return totalOutput <= totalInput;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        List<Transaction> validTxs = new ArrayList<Transaction>();
        for (Transaction tx : possibleTxs) {
            if (isValidTx(tx)) {
                validTxs.add(tx);
                removeConsumedInputsFromPool(tx);
                addCreatedOutputsToPool(tx);
            }
        }
        return validTxs.toArray(new Transaction[validTxs.size()]);
    }

    private void addCreatedOutputsToPool(Transaction tx) {
        List<Transaction.Output> outputs = tx.getOutputs();
        for (int i = 0; i < outputs.size(); i++) {
            Transaction.Output output = outputs.get(i);
            UTXO utxo = new UTXO(tx.getHash(), i);
            uPool.addUTXO(utxo, output);
        }
    }

    private void removeConsumedInputsFromPool(Transaction tx) {
        for (Transaction.Input input : tx.getInputs()) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            uPool.removeUTXO(utxo);
        }
    }

}

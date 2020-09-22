package lab4;

// Класс трехслойной нейронной сети
public class NeuralNet {
    // Поля класса
    public int[] network_layer_sizes;
    public int input_size;
    public int network_size;
    public int output_size;
    public double[][] output;
    public double[][][] weight;
    public double[][] bias;
    public double[][] error;
    public double[][] output_derivative;
    // Конструктор класса
    public NeuralNet(int... network_layer_sizes) {
        this.network_layer_sizes = network_layer_sizes;
        this.input_size = network_layer_sizes[0];
        this.network_size = network_layer_sizes.length;
        this.output_size = network_layer_sizes[this.network_size - 1];
        this.output = new double[network_size][];
        this.output_derivative = new double[network_size][];
        this.error = new double[network_size][];
	this.bias = new double[network_size][];
        this.weight = new double[network_size][][];
        // Инициализация весов слоев нейронной сети
        for (int i = 0; i < network_size; i++) {
            output[i] = new double[network_layer_sizes[i]];
            bias[i] = create_random_single_array(network_layer_sizes[i], -1.0, 1.0);
            error[i] = new double[network_layer_sizes[i]];
            output_derivative[i] = new double[network_layer_sizes[i]];
            if (i > 0) weight[i] = create_random_double_array(network_layer_sizes[i], network_layer_sizes[i - 1], -1.0, 1.0);
        }
    }
    
    // Заполнение одномерного массива случайными числами
    public static double[] create_random_single_array(int size, double lower, double upper) {
        if(size < 1) return null;
        double[] array = new double[size];
        for(int i = 0; i < size; i++) array[i] = Math.random() * (upper - lower) + lower;
        return array;
    }
    
    // Заполнение двумерного массива случайными числами
    public static double[][] create_random_double_array(int x, int y, double lower, double upper) {
        if(x < 1 || y < 1) return null;
        double[][] array = new double[x][y];
        for(int i = 0; i < x; i++) array[i] = create_random_single_array(y, lower, upper);
        return array;
    }
    
    // Метод получения сигмоиды
    public double sigmoid(double x) { return 1d / (1 + Math.exp(-x)); }
    
    // Метод тренировки с помощью обратного распространения ошибки
    public void backprop_train(double input[], double[] result, double eta) {
        // Подсчитаем выходные значения
        this.output[0] = input;
        for (int i = 1; i < network_size; i++) {
            for (int j = 0; j < network_layer_sizes[i]; j++) {
                double current_sum = bias[i][j];
                for (int k = 0; k < network_layer_sizes[i - 1]; k++) current_sum += weight[i][j][k] * output[i - 1][k];
                output[i][j] = sigmoid(current_sum); output_derivative[i][j] = output[i][j] * (1 - output[i][j]);                
            }            
        }
        // Вычислим ошибки
        for (int i = 0; i < network_layer_sizes[network_size - 1]; i++)
            error[network_size - 1][i] = (output[network_size - 1][i] - result[i]) * output_derivative[network_size - 1][i];
        for (int i = network_size - 2; i > 0; i--) {
            for (int j = 0; j < network_layer_sizes[i]; j++) {
                double current_sum = 0.0;
                for(int k = 0; k < network_layer_sizes[i + 1]; k++)
                    current_sum += weight[i + 1][k][j] * error[i + 1][k];
                error[i][j] = current_sum * output_derivative[i][j];
            }
        }
        // Обновим веса слоев нейронной сети
        for (int i = 1; i < network_size; i++) {
            for (int j = 0; j < network_layer_sizes[i]; j++) {
                double delta = -eta * error[i][j]; bias[i][j] += delta;
                for (int k = 0; k <  network_layer_sizes[i - 1]; k++) 
                    weight[i][j][k] += (delta * output[i - 1][k]);
            }
        }
    }
    
    // Метод получения ответа от нейронной сети
    public int ask(double[] input) {
        this.output[0] = input;
        for (int i = 1; i < network_size; i++) {
            for (int j = 0; j < network_layer_sizes[i]; j++) {
                double current_sum = bias[i][j];
                for (int k = 0; k < network_layer_sizes[i - 1]; k++) current_sum += weight[i][j][k] * output[i - 1][k];
                output[i][j] = sigmoid(current_sum); output_derivative[i][j] = output[i][j] * (1 - output[i][j]);
            }
        }
        int j = 0;
        for (int i = 0; i < output[network_size - 1].length; i++)
            if (output[network_size - 1][i] > output[network_size - 1][j]) j = i;
       return j;
    }
    
    public void learning() {
        double symbols[][];
        symbols = new double[][] {
            {0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1},
            {1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0},
            {0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1},
            {1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0}, 
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1}, 
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0}, 
            {1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1}, 
            {1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1}, 
            {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0}, 
            {0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0}, 
            {1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1},
            {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1}, 
            {1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1}, 
            {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1}, 
            {1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1},
            {1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0},
            {1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1},
            {1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1},
            {1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1},
            {1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
            {1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1},
            {1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0},
            {1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
            {1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1}
            };
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 26; j++) {
                double[] result = new double[26];
                result[j] = 1.0;
                for (int k = 0; k < 32; k++) {
                    this.backprop_train(symbols[j], result, 0.25);
                }
            }
        }
    }
}

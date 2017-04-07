package com.example.yihui26.calculator;

import android.icu.text.DecimalFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    //IDs of all numeric buttons
    private int[] numericButtons = {R.id.btnZero, R.id.btnOne, R.id.btnTwo,
        R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix,
            R.id.btnSeven, R.id.btnEight, R.id.btnNine};
    //IDs of all operator buttons
    private int[] operatorButtons = {R.id.btnAdd, R.id.btnSubtract,
        R.id.btnMultiply, R.id.btnDivide};
    //TextView used to display output
    private TextView txtScreen;
    //Represent if the last pressed key is numeric or not
    private boolean lastNumeric;
    //Represent if the current state is an error or not
    private boolean stateError;
    // If true, do not allow to add another dot since there is only once decimal.
    private boolean lastDot;
    // Indicate if it is the first value
    private boolean firstNumber = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the TextView and assign to private txtScreen variable
        this.txtScreen = (TextView) findViewById(R.id.txtScreen);
        //Find and set OnClickListener to numeric buttons - create function
        setNumericOnClickListener();
        //Find and set OnClickListener to operator buttons, equal and dot.
        setOperatorOnClickListener();

    }

    /*------
     * Define OnClickListener function to set OnClickListener to numeric buttons
     ------*/
    private void setNumericOnClickListener(){
        //Create a common OnClickListener
        View.OnClickListener listener = new View.OnClickListener(){
          @Override
            public void onClick(View v){
              //Just append and set the text of the clicked button
              Button button = (Button) v;
              if (stateError){
                  //if current state is error, replace error msg
                  txtScreen.setText(button.getText());
                  stateError=false;
              }else{

                  //check if should clear screen
                  if (firstNumber){
                      txtScreen.setText("");
                      firstNumber = false;
                  }
                  //if not, expression is valid, append to it.
                  txtScreen.append(button.getText());

              }
              //Set flag that last button pressed is numeric
              lastNumeric = true;
          }
        };
        //Assign the listener to all numeric buttons
        for(int id : numericButtons){
            findViewById(id).setOnClickListener(listener);
        }
    }

    /*------
     * Define OnClickListener function to set OnClickListener to operator buttons,
     * equal and dot
     ------*/
    private void setOperatorOnClickListener(){
        //Create a common OnClickListener for operators
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Ignore appending operator if there's error
                //Append the operator only if last input is numeric
                if(lastNumeric && !stateError){
                    Button button = (Button) v;
                    txtScreen.append(button.getText());
                    lastNumeric = false;
                    lastDot = false; //reset dot flag
                }
            }
        };
        //Assign the listener to all operator functions
        for(int id : operatorButtons){
            findViewById(id).setOnClickListener(listener);
        }

        //Decimal points
        findViewById(R.id.btnDot).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                //Append last dot only if there's no error and no previous dots
                if(lastNumeric && !stateError && !lastDot){
                    txtScreen.append(".");
                    lastNumeric = false;
                    lastDot = true;
                }
            }
        });

        //Clear button
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                //Clear the screen
                txtScreen.setText("");
                //Reset all states and flags
                lastNumeric = false;
                stateError = false;
                lastDot = false;
            }
        });

        //Equal button
        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                //Create new function to calculate when the equal button is pressed.
                onEqual();
            }
        });
    }

    /*------
     * Calculate the solution when equal button is pressed
      ------*/
    private void onEqual(){
        //Perform function when there's no error and last function is a number
        if(lastNumeric && !stateError){
            //Read expression
            String appendedTxt = txtScreen.getText().toString();
            //Create an expression from a class at exp4j library
            Expression expression = new ExpressionBuilder(appendedTxt).build();
            try{
                double result = expression.evaluate();
                txtScreen.setText(Double.toString(result));
                lastDot = false;
                firstNumber = true;
            } catch (ArithmeticException ex){
                //Disaplay error message
                txtScreen.setText("Error: "+ex);
                stateError = true;
                lastNumeric = false;
            }
        }
    }

}

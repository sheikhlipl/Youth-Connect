package com.lipl.youthconnect.youth_connect.util;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.ReportFromDetailsActivity;
import com.lipl.youthconnect.youth_connect.adapter.DropDownListAdapter;
import com.lipl.youthconnect.youth_connect.pojo.FieldDetails;
import com.lipl.youthconnect.youth_connect.pojo.FieldOptions;
import com.lipl.youthconnect.youth_connect.pojo.FiledValidation;
import com.lipl.youthconnect.youth_connect.pojo.FormField;
import com.lipl.youthconnect.youth_connect.pojo.Options;
import com.lipl.youthconnect.youth_connect.pojo.ReportForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suhasini on 30-Apr-15.
 */
public class DynamicForm {

    //private static ReportForm mReportForm = null;
    public enum FieldType {
        TEXTBOX, TEXTAREA, FILE, SNAP, DATE, SELECT, CHECKBOX, RADIO
    }
    private static FormField mFormField;
    private static FieldDetails mFieldDetails;
    private static List<FieldDetails> fieldDetailsList;
    public static void generateForm(ReportForm rForm, List<FieldDetails> _formFieldDetailsList, final ReportFromDetailsActivity context) {
        final ReportForm mReportForm = rForm;

        LinearLayout parent = (LinearLayout) context.findViewById(R.id.layoutFormParent);
        parent.removeAllViews();

        if(_formFieldDetailsList != null){
            fieldDetailsList = _formFieldDetailsList;
        } else{
            fieldDetailsList = new ArrayList<FieldDetails>();
            for(int count = 0; count > mReportForm.getFormFieldList().size(); count++){
                fieldDetailsList.add(mReportForm.getFormFieldList().get(count).getFieldDetails());
            }
        }

        List<FormField> formFieldList = mReportForm.getFormFieldList();
        for (int i = 0; i < formFieldList.size(); i++) {
            mFormField = formFieldList.get(i);
            if(_formFieldDetailsList != null) {
                mFieldDetails = fieldDetailsList.get(i);
            } else{
                mFieldDetails = formFieldList.get(i).getFieldDetails();
            }

            int fieldId = mFieldDetails.getFieldID();
            FieldType fieldType = mFieldDetails.getFieldType();
            String labelName = mFieldDetails.getLabelName();
            String fieldDefaultValue = mFieldDetails.getFieldDefaultValue();
            String fieldInputedValue = mFieldDetails.getFieldValue();
            String is_multiple = mFieldDetails.getIs_multiple();
            FiledValidation validation = mFieldDetails.getFieldValidation();
            String min_range = validation.getMin_range();
            String max_range = validation.getMax_range();
            String validation_type = validation.getValidation_type();
            String field_is_compulsory = validation.getField_is_compulsory();

            FieldOptions fieldOptions = mFieldDetails.getFieldOptions();
            List<Options> opList = fieldOptions.getOptions();

            int padding = 10;
            TextView textViewLabel = new TextView(context);
            textViewLabel.setPadding(0, padding, 0, padding);
            textViewLabel.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            if(field_is_compulsory.equalsIgnoreCase("y")) {
                String htext = labelName+" "+Html.fromHtml(context.getResources().getString(R.string.ast));
                textViewLabel.setText(htext, TextView.BufferType.SPANNABLE);
            } else {
                textViewLabel.setText(labelName);
            }
            textViewLabel.setTypeface(Typeface.DEFAULT_BOLD);
            textViewLabel.setTextColor(Color.WHITE);
            parent.addView(textViewLabel);

            EditText editText = new EditText(context);
            editText.setPadding(padding, padding, padding, padding);
            editText.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            editText.setHint(labelName);
            editText.setTextColor(Color.WHITE);
            editText.setHintTextColor(Color.WHITE);
            if(mFieldDetails.isInvalid() == false) {
                editText.setBackgroundResource(R.drawable.white_bg_rect);
            } else{
                editText.setBackgroundResource(R.drawable.white_bg_rect_with_red_stroke);
            }

            switch(fieldType){
                case TEXTBOX:

                    editText.setSingleLine(true);
                    List<View> viewList = new ArrayList<View>();
                    viewList.add(editText);
                    mFormField.setViewList(viewList);
                    parent.addView(editText);

                    if(validation != null && validation.getValidation_type() != null &&
                            validation.getValidation_type().equalsIgnoreCase("Email") ){
                        editText.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    } else if(validation != null && validation.getValidation_type() != null
                            && (validation.getValidation_type().equalsIgnoreCase("Numeric")
                            || validation.getValidation_type().equalsIgnoreCase("Mobile"))){
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else if(validation != null && validation.getValidation_type() != null &&
                            validation.getValidation_type().equalsIgnoreCase("Name") ){
                        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    } else if(validation != null && validation.getValidation_type() != null &&
                            validation.getValidation_type().equalsIgnoreCase("Address") ){
                        editText.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                    } else if(validation != null && validation.getValidation_type() != null &&
                            validation.getValidation_type().equalsIgnoreCase("Pin") ){
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else if(validation != null && validation.getValidation_type() != null &&
                            validation.getValidation_type().equalsIgnoreCase("Decimal") ){
                        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL
                                | InputType.TYPE_CLASS_NUMBER
                                | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    } else {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                    }

                    try {
                        int maxRange = (int) Long.parseLong(validation.getMax_range());
                        if(maxRange > 0) {
                            InputFilter[] FilterArray = new InputFilter[1];
                            FilterArray[0] = new InputFilter.LengthFilter(maxRange);
                            editText.setFilters(FilterArray);
                        }
                    } catch(Exception e){

                    }

                    editText.setText(fieldInputedValue);
                    editText.setHint(fieldDefaultValue);

                    break;
                case TEXTAREA:

                    editText.setSingleLine(false);
                    try {
                        int maxRange = (int) Long.parseLong(validation.getMax_range());
                        if(maxRange > 0) {
                            InputFilter[] FilterArray = new InputFilter[1];
                            FilterArray[0] = new InputFilter.LengthFilter(maxRange);
                            editText.setFilters(FilterArray);
                        }
                    } catch(Exception e){

                    }
                    List<View> viewListTA = new ArrayList<View>();
                    viewListTA.add(editText);
                    mFormField.setViewList(viewListTA);
                    parent.addView(editText);

                    editText.setText(fieldInputedValue);
                    editText.setTextColor(Color.WHITE);
                    editText.setHintTextColor(Color.WHITE);
                    editText.setHint(fieldDefaultValue);

                    break;
                case FILE:

                    LinearLayout lRLayout1 = new LinearLayout(context);
                    lRLayout1.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    lRLayout1.setOrientation(LinearLayout.VERTICAL);

                    final TextView tvTypesFile = new TextView(context);
                    tvTypesFile.setPadding(padding, 0, padding, padding);
                    tvTypesFile.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    String filesCanUpload = "(bmp, "+"doc, "+"docx, "+"gif, "+"html, "+
                            "jpeg, "+"jpg, "+"pdf, "+"png, "+"xls, "+"xlsx, "+"rar, "+"zip, "+"txt)";
                    tvTypesFile.setText(filesCanUpload);
                    tvTypesFile.setTextColor(context.getResources().getColor(R.color.red));
                    lRLayout1.addView(tvTypesFile);

                    LinearLayout lRLayout = new LinearLayout(context);
                    lRLayout.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    lRLayout.setOrientation(LinearLayout.VERTICAL);

                    final TextView editTextFileUpload = new TextView(context);
                    editTextFileUpload.setPadding(padding, padding, padding, padding);
                    editTextFileUpload.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    if(mFieldDetails.isInvalid() == false) {
                        editTextFileUpload.setBackgroundResource(R.drawable.white_bg_rect);
                    } else{
                        editTextFileUpload.setBackgroundResource(R.drawable.white_bg_rect_with_red_stroke);
                    }
                    editTextFileUpload.setTextColor(Color.WHITE);
                    editTextFileUpload.setHintTextColor(Color.WHITE);
                    lRLayout.addView(editTextFileUpload);

                    TextView btnFileUpload = new TextView(context);
                    btnFileUpload.setPadding(padding, padding, padding, padding);
                    btnFileUpload.setBackgroundColor(context.getResources().getColor(R.color.login_btn_color_pressed));
                    btnFileUpload.setTextColor(Color.WHITE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    btnFileUpload.setLayoutParams(lp);
                    lp.setMargins(0, padding, 0, 0);
                    btnFileUpload.setText("Upload File");
                    lRLayout.addView(btnFileUpload);
                    lRLayout1.addView(lRLayout);
                    parent.addView(lRLayout1);
                    btnFileUpload.setTag(mFieldDetails.getFieldID());
                    btnFileUpload.setTextColor(Color.WHITE);
                    btnFileUpload.setHintTextColor(Color.WHITE);

                    btnFileUpload.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int id =  (Integer) (v.getTag());
                            ArrayList<FieldDetails> fieldDetailsesList = saveFieldInputedValues(mReportForm);
                            context.getFilesFromSdCard(fieldDetailsesList, id);
                        }
                    });

                    List<View> viewListFile = new ArrayList<View>();
                    viewListFile.add(editTextFileUpload);
                    mFormField.setViewList(viewListFile);
                    editTextFileUpload.setText(fieldInputedValue);
                    editTextFileUpload.setHint("Select file to upload");

                    break;
                case DATE:

                    LinearLayout dLLayout = new LinearLayout(context);
                    dLLayout.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    dLLayout.setOrientation(LinearLayout.HORIZONTAL);

                    final TextView tvDate = new TextView(context);
                    tvDate.setBackgroundResource(R.drawable.white_bg_rect);
                    tvDate.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    tvDate.setPadding(padding, padding, padding, padding);
                    if(fieldInputedValue != null && fieldInputedValue.trim().length() > 0){
                        tvDate.setText(fieldInputedValue);
                    }else{
                        tvDate.setHint("Select Date");
                    }
                    tvDate.setTextColor(Color.WHITE);
                    tvDate.setHintTextColor(Color.WHITE);
                    dLLayout.addView(tvDate);

                    TextView tvSelectDate = new TextView(context);
                    LinearLayout.LayoutParams lpSD = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lpSD.setMargins(padding, 0, 0, 0);
                    tvSelectDate.setText("Date");
                    tvSelectDate.setTextColor(context.getResources().getColor(android.R.color.white));
                    tvSelectDate.setLayoutParams(lpSD);
                    tvSelectDate.setPadding(padding, padding, padding, padding);
                    tvSelectDate.setBackgroundColor(context.getResources().getColor(R.color.login_btn_color_pressed));
                    tvSelectDate.setTextColor(Color.WHITE);
                    tvSelectDate.setTextColor(Color.WHITE);
                    tvSelectDate.setHintTextColor(Color.WHITE);
                    dLLayout.addView(tvSelectDate);

                    tvSelectDate.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                      int dayOfMonth) {
                                    int month = monthOfYear + 1;
                                    tvDate.setText(dayOfMonth + "/" + month + "/" + year);
                                }
                            }, Util.getCurrentYear(), Util.getCurrentMonth(), Util.geCurrentDay());
                            datePickerDialog.show();
                        }
                    });
                    List<View> viewListDate = new ArrayList<View>();
                    viewListDate.add(tvDate);
                    mFormField.setViewList(viewListDate);
                    parent.addView(dLLayout);

                    break;
                case SELECT:

                    Spinner spinner = new Spinner(context);
                    spinner.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    int selectPos = -1;
                    List<String> optNameList = new ArrayList<String>();
                    optNameList.add("Select");
                    for (int count = 0; count < opList.size(); count++){
                        optNameList.add(opList.get(count).getOptionName());
                        if(opList.get(count).isSelected()){
                            selectPos = count + 1;
                        }
                    }

                    DropDownListAdapter adapter =  new DropDownListAdapter(context, optNameList);
                    spinner.setAdapter(adapter);
                    if(selectPos >= 0){
                        spinner.setSelection(selectPos);
                    }
                    List<View> viewListSpinner = new ArrayList<View>();
                    viewListSpinner.add(spinner);
                    mFormField.setViewList(viewListSpinner);
                    parent.addView(spinner);

                    break;
                case CHECKBOX:

                    LinearLayout chkBxlRLayout = new LinearLayout(context);
                    chkBxlRLayout.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    chkBxlRLayout.setOrientation(LinearLayout.VERTICAL);

                    List<View> viewListCheckBox = new ArrayList<View>();
                    for (int count = 0; count < opList.size(); count++){
                        CheckBox checkBox = new CheckBox(context);
                        checkBox.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        checkBox.setText(opList.get(count).getOptionName());
                        if(opList.get(count).isSelected()){
                            checkBox.setChecked(true);
                        } else {
                            checkBox.setChecked(false);
                        }
                        checkBox.setTextColor(Color.WHITE);
                        chkBxlRLayout.addView(checkBox);
                        viewListCheckBox.add(checkBox);

                    }

                    formFieldList.get(i).setViewList(viewListCheckBox);
                    parent.addView(chkBxlRLayout);

                    break;
                case RADIO:

                    RadioGroup radioGroup = new RadioGroup(context);
                    radioGroup.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    radioGroup.setOrientation(LinearLayout.VERTICAL);
                    List<View> viewListRadio = new ArrayList<View>();
                    for (int count = 0; count < opList.size(); count++){
                        RadioButton radioButton = new RadioButton(context);
                        radioButton.setId(opList.get(count).getOptionId());
                        radioButton.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        radioButton.setText(opList.get(count).getOptionName());
                        radioButton.setTextColor(Color.WHITE);
                        if(opList.get(count).isSelected()){
                            radioButton.setChecked(true);
                        } else{
                            radioButton.setChecked(false);
                        }
                        radioGroup.addView(radioButton);
                        viewListRadio.add(radioButton);
                    }
                    mFormField.setViewList(viewListRadio);
                    parent.addView(radioGroup);
                    break;
                default:
                    break;
            }

            if(mFieldDetails.isInvalid() == true) {
                TextView textViewErrorLabel = new TextView(context);
                textViewErrorLabel.setPadding(0, padding, 0, padding);
                textViewErrorLabel.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                textViewErrorLabel.setText(mFieldDetails.getErrorMsg());
                textViewErrorLabel.setTextColor(context.getResources().getColor(R.color.red));
                textViewErrorLabel.setTypeface(Typeface.DEFAULT_BOLD);
                parent.addView(textViewErrorLabel);
            }
        }
    }

    public static ArrayList<FieldDetails> saveFieldInputedValues(ReportForm mReportForm){
        ArrayList<FieldDetails> fieldDetailsesList = new ArrayList<FieldDetails>();

        if(mReportForm != null && mReportForm.getFormFieldList() != null
                && mReportForm.getFormFieldList().size() > 0) {

            List<FormField> _fieldDetailsList = mReportForm.getFormFieldList();
            for (int count = 0; count < _fieldDetailsList.size(); count++) {
                FormField formField = _fieldDetailsList.get(count);
                FieldDetails fieldDetails = formField.getFieldDetails();
                if (fieldDetails.getFieldType() == FieldType.TEXTBOX ||
                        fieldDetails.getFieldType() == FieldType.TEXTAREA ||
                        fieldDetails.getFieldType() == FieldType.FILE ||
                        fieldDetails.getFieldType() == FieldType.SNAP ||
                        fieldDetails.getFieldType() == FieldType.DATE) {
                    String val = null;

                    for (int k = 0; k < formField.getViewList().size(); k++) {
                        View view = formField.getViewList().get(k);
                        if (view instanceof EditText) {
                            val = ((EditText) view).getText().toString().trim();
                        } else if (view instanceof TextView) {
                            val = ((TextView) view).getText().toString().trim();
                        } else {
                            val = "Empty Val";
                        }
                    }

                    fieldDetails.setFieldValue(val);

                } else if (fieldDetails.getFieldType() == FieldType.SELECT ||
                        fieldDetails.getFieldType() == FieldType.CHECKBOX ||
                        fieldDetails.getFieldType() == FieldType.RADIO) {

                    String val = "";
                    if ((fieldDetails.getFieldType() == FieldType.SELECT)) {
                        for (int k = 0; k < formField.getViewList().size(); k++) {
                            View view = formField.getViewList().get(k);
                            if (view instanceof Spinner) {

                                String selectedOptionVal = (String) ((Spinner) view).getSelectedItem();
                                for (int cnt = 0;
                                     cnt < fieldDetails.getFieldOptions().getOptions().size();
                                     cnt++) {
                                    String optoinName = fieldDetails.getFieldOptions().getOptions().get(cnt).getOptionName();
                                    if (optoinName.equalsIgnoreCase(selectedOptionVal)) {
                                        fieldDetails.getFieldOptions().getOptions().get(cnt).setSelected(true);
                                    } else{
                                        fieldDetails.getFieldOptions().getOptions().get(cnt).setSelected(false);
                                    }
                                }
                            }
                        }

                    } else if (fieldDetails.getFieldType() == FieldType.CHECKBOX) {
                        for (int k = 0; k < formField.getViewList().size(); k++) {
                            View view = formField.getViewList().get(k);
                            if (view instanceof CheckBox) {
                                CheckBox chkBox = (CheckBox) view;
                                for (int cnt = 0; cnt < fieldDetails.getFieldOptions().getOptions().size(); cnt++) {
                                    Options option = fieldDetails.getFieldOptions().getOptions().get(cnt);
                                    if (chkBox.getText().toString().trim().equalsIgnoreCase(option.getOptionName())){
                                        if (chkBox.isChecked()) {
                                            option.setSelected(true);
                                        } else{
                                            option.setSelected(false);
                                        }
                                    }
                                }
                            }
                        }

                    } else if (fieldDetails.getFieldType() == FieldType.RADIO) {
                        for (int k = 0; k < formField.getViewList().size(); k++) {
                            View view = formField.getViewList().get(k);
                            if (view instanceof RadioButton) {
                                RadioButton radioBtn = ((RadioButton) view);
                                for (int cnt = 0; cnt < fieldDetails.getFieldOptions().getOptions().size(); cnt++) {
                                    Options option = fieldDetails.getFieldOptions().getOptions().get(cnt);
                                    if (radioBtn.getText().toString().trim().equalsIgnoreCase(option.getOptionName())){
                                        if (radioBtn.isChecked()) {
                                            option.setSelected(true);
                                        } else{
                                            option.setSelected(false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                fieldDetailsesList.add(fieldDetails);
            }
        }
        return fieldDetailsesList;
    }
}
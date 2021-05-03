package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName
import com.stripe.android.PaymentConfiguration
import java.io.Serializable

class PostJobPostDto : Serializable {
    var jobDescription: String? = ""
    var jobTime: String? = ""
    var jobAmount: String? = ""
    var grandTotal: String? = ""
    var jobPaymentMode: String? = ""
    var job_tax_amount: String? = ""
    var Charge_for_Jobs: String? = ""
    var Charge_for_Jobs_percentage: String? = ""
    var Charge_for_Jobs_Admin_percentage: String? = ""
    var Charge_for_Jobs_Delivery_percentage: String? = ""
    var admin_service_fees: String? = ""
    var delivery_employee_fee: String? = ""
    var jobId: String? = ""
}

class ServicesChargesDtoMain : BaseResponseDto() {
    @SerializedName("data")
    var data: ServicesChargesDtoData? = ServicesChargesDtoData()
}

class ServicesChargesDtoData {
    @SerializedName("Charge_for_Jobs")
    var Charge_for_Jobs: String? = ""

    @SerializedName("Charge_for_Jobs_percentage")
    var Charge_for_Jobs_percentage: String? = ""

    @SerializedName("admin_service_fees")
    var admin_service_fees: String? = ""

    @SerializedName("delivery_employee_fee")
    var delivery_employee_fee: String? = ""

    @SerializedName("Charge_for_Jobs_Admin_percentage")
    var Charge_for_Jobs_Admin_percentage: String? = ""

    @SerializedName("Charge_for_Jobs_Delivery_percentage")
    var Charge_for_Jobs_Delivery_percentage: String? = ""

    @SerializedName("job_tax_amount")
    var job_tax_amount: String? = "0"
}


class PaymentConfigurationMain : BaseResponseDto() {
    @SerializedName("data")
    var data: PaymentConfigurationData? = PaymentConfigurationData()
}

class PaymentConfigurationData {
    @SerializedName("stripe_publishKey")
    var stripe_publishKey: String? = ""

    @SerializedName("stripe_APIKey")
    var stripe_APIKey: String? = ""

    @SerializedName("paypal_client_ID")
    var paypal_client_ID: String? = ""

    @SerializedName("paypal_secret")
    var paypal_secret: String? = ""
}

object PaymentConfigurationSingleton {
    lateinit var paymentConfiguration: PaymentConfigurationData
}


class PaymentTokenMain : BaseResponseDto() {
    @SerializedName("data")
    var data: PaymentTokenData? = PaymentTokenData()
}

class PaymentTokenData {

}

class PostJobDataMain : BaseResponseDto() {
    @SerializedName("data")
    var data: PostJobDataDto? = PostJobDataDto()
}

class PostJobDataDto {
    @SerializedName("job_order_id")
    var job_order_id: String? = ""

    @SerializedName("employee_name")
    var employee_name: String? = ""

    @SerializedName("job_total_amount")
    var job_total_amount: String? = ""

    @SerializedName("job_offer_time")
    var job_offer_time: String? = ""

    @SerializedName("about_job")
    var about_job: String? = ""

    @SerializedName("Charge_for_Jobs")
    var Charge_for_Jobs: String? = ""

    @SerializedName("Charge_for_Jobs_percentage")
    var Charge_for_Jobs_percentage: String? = ""

    @SerializedName("LoginId")
    var LoginId: String? = ""

    @SerializedName("thank_you_title")
    var thank_you_title: String? = ""

    @SerializedName("thank_you_content")
    var thank_you_content: String? = ""
}

class GetJobDetailsMain : BaseResponseDto() {
    @SerializedName("data")
    var data: GetJobDetailsData = GetJobDetailsData()
}

class GetJobDetailsData {

}

class CancelJobDtoMain:BaseResponseDto(){
    @SerializedName("data")
    var data:CancelJobDtoData = CancelJobDtoData()
}

class CancelJobDtoData{

}
class UpdateJobDtoMain:BaseResponseDto(){
    @SerializedName("data")
    var data:UpdateJobDtoData = UpdateJobDtoData()
}

class UpdateJobDtoData{

}

class WriteReviewJobDtoMain:BaseResponseDto(){
    @SerializedName("data")
    var data:WriteReviewJobDtoData = WriteReviewJobDtoData()
}

class WriteReviewJobDtoData{

}



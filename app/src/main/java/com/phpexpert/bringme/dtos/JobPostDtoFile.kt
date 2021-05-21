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

class CancelJobDtoMain : BaseResponseDto() {
    @SerializedName("data")
    var data: CancelJobDtoData = CancelJobDtoData()
}

class CancelJobDtoData {

}

class UpdateJobDtoMain : BaseResponseDto() {
    @SerializedName("data")
    var data: UpdateJobDtoData = UpdateJobDtoData()
}

class UpdateJobDtoData {

}

class WriteReviewJobDtoMain : BaseResponseDto() {
    @SerializedName("data")
    var data: WriteReviewJobDtoData = WriteReviewJobDtoData()
}

class WriteReviewJobDtoData {

}

class JobDetailsDtoMain : BaseResponseDto() {
    @SerializedName("data")
    var data: JobDetailsDtoData? = JobDetailsDtoData()
}

class JobDetailsDtoData {
    @SerializedName("OrderDetailList")
    var OrderDetailList: ArrayList<JobDetailsDtoList>? = ArrayList()
}

class JobDetailsDtoList {
    @SerializedName("order_id")
    var order_id: String? = ""

    @SerializedName("job_order_id")
    var job_order_id: String? = ""

    @SerializedName("job_OTP_code")
    var job_OTP_code: String? = ""

    @SerializedName("about_job")
    var about_job: String? = ""

    @SerializedName("job_offer_time")
    var job_offer_time: String? = ""

    @SerializedName("job_sub_total")
    var job_sub_total: String? = ""

    @SerializedName("job_tax_amount")
    var job_tax_amount: String? = ""

    @SerializedName("admin_service_fees")
    var admin_service_fees: String? = ""

    @SerializedName("delivery_employee_fee")
    var delivery_employee_fee: String? = ""

    @SerializedName("job_total_amount")
    var job_total_amount: String? = ""

    @SerializedName("payment_mode")
    var payment_mode: String? = ""

    @SerializedName("payment_status")
    var payment_status: String? = ""

    @SerializedName("job_post_date")
    var job_post_date: String? = ""

    @SerializedName("job_posted_time")
    var job_posted_time: String? = ""

    @SerializedName("payment_transaction_id")
    var payment_transaction_id: String? = ""

    @SerializedName("job_posted_country")
    var job_posted_country: String? = ""

    @SerializedName("job_posted_state")
    var job_posted_state: String? = ""

    @SerializedName("job_posted_city")
    var job_posted_city: String? = ""

    @SerializedName("job_posted_lat")
    var job_posted_lat: String? = ""

    @SerializedName("job_posted_lang")
    var job_posted_lang: String? = ""

    @SerializedName("job_posted_zipcode")
    var job_posted_zipcode: String? = ""

    @SerializedName("job_posted_address")
    var job_posted_address: String? = ""

    @SerializedName("Client_name")
    var Client_name: String? = ""

    @SerializedName("Client_email")
    var Client_email: String? = ""

    @SerializedName("Client_country")
    var Client_country: String? = ""

    @SerializedName("Client_state")
    var Client_state: String? = ""

    @SerializedName("Client_city")
    var Client_city: String? = ""

    @SerializedName("Client_address")
    var Client_address: String? = ""

    @SerializedName("Client_lat")
    var Client_lat: String? = ""

    @SerializedName("Client_long")
    var Client_long: String? = ""

    @SerializedName("Client_postcode")
    var Client_postcode: String? = ""

    @SerializedName("Client_phone")
    var Client_phone: String? = ""

    @SerializedName("Client_phone_code")
    var Client_phone_code: String? = ""

    @SerializedName("Client_photo")
    var Client_photo: String? = ""

    @SerializedName("Delivery_Employee_name")
    var Delivery_Employee_name: String? = ""

    @SerializedName("Delivery_Employee_email")
    var Delivery_Employee_email: String? = ""

    @SerializedName("Delivery_Employee_country")
    var Delivery_Employee_country: String? = ""

    @SerializedName("Delivery_Employee_state")
    var Delivery_Employee_state: String? = ""

    @SerializedName("Delivery_Employee_city")
    var Delivery_Employee_city: String? = ""

    @SerializedName("Delivery_Employee_address")
    var Delivery_Employee_address: String? = ""

    @SerializedName("Delivery_Employee_lat")
    var Delivery_Employee_lat: String? = ""

    @SerializedName("Delivery_Employee_long")
    var Delivery_Employee_long: String? = ""

    @SerializedName("Delivery_Employee_postcode")
    var Delivery_Employee_postcode: String? = ""

    @SerializedName("Delivery_Employee_phone")
    var Delivery_Employee_phone: String? = ""

    @SerializedName("Delivery_Employee_phone_code")
    var Delivery_Employee_phone_code: String? = ""

    @SerializedName("Delivery_Employee_photo")
    var Delivery_Employee_photo: String? = ""

    @SerializedName("job_review_description")
    var job_review_description: String? = ""

    @SerializedName("job_rating")
    var job_rating: String? = ""

    @SerializedName("job_review_date")
    var job_review_date: String? = ""

    @SerializedName("job_review_time")
    var job_review_time: String? = ""

    @SerializedName("job_accept_date")
    var job_accept_date: String? = ""

    @SerializedName("job_accept_time")
    var job_accept_time: String? = ""

    @SerializedName("job_completed_date")
    var job_completed_date: String? = ""

    @SerializedName("job_completed_time")
    var job_completed_time: String? = ""

    @SerializedName("order_status_icon")
    var order_status_icon: String? = ""

    @SerializedName("order_status_msg")
    var order_status_msg: String? = ""

    @SerializedName("order_status_close")
    var order_status_close: String? = ""

    @SerializedName("order_status_color_code")
    var order_status_color_code: String? = ""

    @SerializedName("order_status_text_color_code")
    var order_status_text_color_code: String? = ""

    @SerializedName("write_review_status")
    var write_review_status: String? = ""

    @SerializedName("thank_you_title")
    var thank_you_title: String? = ""

    @SerializedName("thank_you_content")
    var thank_you_content: String? = ""

    @SerializedName("review_status")
    var review_status: String? = ""

    @SerializedName("order_decline_reason")
    var order_decline_reason: String? = ""
}



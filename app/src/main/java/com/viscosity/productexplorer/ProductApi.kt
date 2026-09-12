package com.viscosity.productexplorer

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query



interface ProductApi {

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductResponse

    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") productId: Int
    ): Product

}

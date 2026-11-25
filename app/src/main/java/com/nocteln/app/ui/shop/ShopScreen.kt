package com.nocteln.app.ui.shop

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nocteln.app.R

@Composable
fun ShopScreen(modifier: Modifier = Modifier)
{
    Text(text = stringResource(R.string.shop_title))
}
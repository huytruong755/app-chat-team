package com.example.mychatapp.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mychatapp.R


//Hình minh họa
//Tiêu đề
//Nút

@Composable
fun OnboardingScreen(navController: NavController) {
    //dọc
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top

    ) {
        //image
        Spacer(modifier = Modifier.height(135.dp))


        Image(
            painter = painterResource(id = R.drawable.circlebg1),
            contentDescription = "Logo 2",
            modifier = Modifier
                .width(117.46.dp)
                .height(117.46.dp)
                .offset(x = (-70).dp, y = (50).dp)
        )

        Image(
            painter = painterResource(id = R.drawable.member1),
            contentDescription = "Logo 1",
            modifier = Modifier
                .width(149.98.dp)
                .height(228.dp)
                .offset(x = (-62).dp, y = (-60).dp)

        )
        Image(
            painter = painterResource(id = R.drawable.chat1),
            contentDescription = "Logo 1",
            modifier = Modifier
                .width(53.dp)
                .height(28.67.dp)
                .offset(x = (-25).dp, y = (-295).dp)

        )
        Image(
            painter = painterResource(id = R.drawable.circlebg2),
            contentDescription = "Logo 2",
            modifier = Modifier
                .width(117.46.dp)
                .height(117.46.dp)
                .offset(x = (90).dp, y = (-385).dp)
        )
        Image(
            painter = painterResource(id = R.drawable.chat2),
            contentDescription = "Logo 2",
            modifier = Modifier
                .width(47.dp)
                .height(25.42.dp)
                .offset(x = (50).dp, y = (-500).dp)
        )


        Text(
            text = "Connect easily with\nyour family and friends\nover countries",
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF000000),
            lineHeight = 38.sp,
            modifier = Modifier
                .width(350.dp)
                .offset(y = (-180).dp)
        )

        Text(
            text = "Terms & Privacy Policy",
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF000000),
            lineHeight = 28.sp,
            modifier = Modifier
                .width(280.dp)
                .offset(y = (-60).dp)
        )
        val onStartClicked =
            {
            navController.navigate("PhoneNumber")
        }
        Button(
            onClick = onStartClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
                .offset(y = (-45).dp)
        ) {
            Text(
                text = "Start Messaging",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                .offset(y = (-1.8).dp)
            )
        }
    }
}


// src/pages/Checkout.tsx
import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Grid,
  TextField,
  Alert,
  Stepper,
  Step,
  StepLabel,
  Paper,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
} from '@mui/material';
import { api, endpoints } from '../api/client';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

const steps = ['Review Selection', 'Payment Details', 'Confirmation'];

const Checkout: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [activeStep, setActiveStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [orderId, setOrderId] = useState<string>('');

  // Get data from navigation state
  const selectedOption = location.state?.selectedOption;
  const selectedRecommendation = location.state?.selectedRecommendation;
  const simulationRequest = location.state?.simulationRequest;
  const simulationData = location.state?.simulationData;

  const [paymentDetails, setPaymentDetails] = useState({
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    cardholderName: '',
  });

  if (!selectedOption && !selectedRecommendation) {
    navigate('/');
    return null;
  }

  const finalOption = selectedOption || selectedRecommendation;
  const isFromRecommendation = !!selectedRecommendation;

  const handleNext = () => setActiveStep((prev) => prev + 1);
  const handleBack = () => setActiveStep((prev) => prev - 1);

  const handlePaymentSubmit = async () => {
    try {
      setLoading(true);
      setError(null);

      const checkoutRequest = {
        userId: simulationRequest?.userId || 1,
        selectedOption: finalOption,
        paymentDetails: {
          cardNumber: paymentDetails.cardNumber.slice(-4), // Only send last 4 digits
          cardholderName: paymentDetails.cardholderName,
        },
        tripDetails: simulationRequest,
        totalCost: finalOption.totalCost,
        currency: finalOption.currency,
      };

      const response = await api.post(endpoints.checkout, checkoutRequest);
      setOrderId(response.data.order_id || 'MOCK-12345');
      setSuccess(true);
      handleNext();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Checkout failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const renderStepContent = (step: number) => {
    switch (step) {
      case 0:
        return (
          <Box>
            <Typography variant="h5" gutterBottom>
              Review Your Selection
            </Typography>

            <Card variant="outlined" sx={{ mb: 3 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Selected Option
                </Typography>
                <Grid container spacing={2}>
                  <Grid size={{ xs: 12, md: 6 }}>
                    <Typography variant="body2" color="text.secondary">
                      Type: {finalOption.kind === 'pack' ? 'Roaming Pack' : 'Pay-as-you-go'}
                    </Typography>
                    {finalOption.kind === 'pack' && (
                      <Typography variant="body2" color="text.secondary">
                        Pack ID: {finalOption.packId}
                      </Typography>
                    )}
                    <Typography variant="body2" color="text.secondary">
                      Number of Packs: {finalOption.nPacks || 1}
                    </Typography>
                  </Grid>
                  <Grid size={{ xs: 12, md: 6 }}>
                    <Typography variant="h4" color="primary">
                      {finalOption.totalCost.toFixed(2)} {finalOption.currency}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Total Cost
                    </Typography>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>

            {simulationRequest && (
              <Card variant="outlined" sx={{ mb: 3 }}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Trip Summary
                  </Typography>
                  <Grid container spacing={2}>
                    <Grid size={{ xs: 12, md: 4 }}>
                      <Typography variant="body2" color="text.secondary">
                        Duration: {simulationData?.summary?.days || 0} days
                      </Typography>
                    </Grid>
                    <Grid size={{ xs: 12, md: 4 }}>
                      <Typography variant="body2" color="text.secondary">
                        Data Need: {simulationData?.summary?.totalNeed?.gb?.toFixed?.(2) || 0} GB
                      </Typography>
                    </Grid>
                    <Grid size={{ xs: 12, md: 4 }}>
                      <Typography variant="body2" color="text.secondary">
                        Voice Need: {simulationData?.summary?.totalNeed?.min || 0} min
                      </Typography>
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            )}

            {isFromRecommendation && (
              <Alert severity="info" sx={{ mb: 2 }}>
                This option was recommended based on your usage patterns and trip details.
              </Alert>
            )}
          </Box>
        );

      case 1:
        return (
          <Box>
            <Typography variant="h5" gutterBottom>
              Payment Details
            </Typography>

            <Grid container spacing={3}>
              <Grid size={{ xs: 12 }}>
                <TextField
                  fullWidth
                  label="Card Number"
                  value={paymentDetails.cardNumber}
                  onChange={(e) => setPaymentDetails({ ...paymentDetails, cardNumber: e.target.value })}
                  placeholder="1234 5678 9012 3456"
                />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <TextField
                  fullWidth
                  label="Expiry Date"
                  value={paymentDetails.expiryDate}
                  onChange={(e) => setPaymentDetails({ ...paymentDetails, expiryDate: e.target.value })}
                  placeholder="MM/YY"
                />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <TextField
                  fullWidth
                  label="CVV"
                  value={paymentDetails.cvv}
                  onChange={(e) => setPaymentDetails({ ...paymentDetails, cvv: e.target.value })}
                  placeholder="123"
                />
              </Grid>
              <Grid size={{ xs: 12 }}>
                <TextField
                  fullWidth
                  label="Cardholder Name"
                  value={paymentDetails.cardholderName}
                  onChange={(e) => setPaymentDetails({ ...paymentDetails, cardholderName: e.target.value })}
                  placeholder="John Doe"
                />
              </Grid>
            </Grid>

            {error && (
              <Alert severity="error" sx={{ mt: 2 }}>
                {error}
              </Alert>
            )}
          </Box>
        );

      case 2:
        return (
          <Box textAlign="center">
            <CheckCircleIcon sx={{ fontSize: 80, color: 'success.main', mb: 2 }} />
            <Typography variant="h4" gutterBottom color="success.main">
              Order Confirmed!
            </Typography>
            <Typography variant="h6" gutterBottom>
              Order ID: {orderId}
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
              Your roaming plan has been successfully activated. You will receive a confirmation email shortly.
            </Typography>

            <Paper variant="outlined" sx={{ p: 3, mb: 3, textAlign: 'left' }}>
              <Typography variant="h6" gutterBottom>
                What's Next?
              </Typography>
              <List>
                <ListItem>
                  <ListItemIcon>
                    <CheckCircleIcon color="success" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Immediate Activation"
                    secondary="Your roaming plan is now active and ready to use"
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <CheckCircleIcon color="success" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Email Confirmation"
                    secondary="Check your email for detailed plan information"
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <CheckCircleIcon color="success" />
                  </ListItemIcon>
                  <ListItemText
                    primary="24/7 Support"
                    secondary="Contact our support team if you need assistance"
                  />
                </ListItem>
              </List>
            </Paper>
          </Box>
        );

      default:
        return null;
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate(isFromRecommendation ? '/recommendations' : '/simulation')}
          sx={{ mr: 2 }}
        >
          Back
        </Button>
        <Typography variant="h4">Checkout</Typography>
      </Box>

      <Card>
        <CardContent>
          <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>

          {renderStepContent(activeStep)}

          <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
            <Button disabled={activeStep === 0} onClick={handleBack}>
              Back
            </Button>

            <Box>
              {activeStep === steps.length - 1 ? (
                <Button variant="contained" onClick={() => navigate('/')}>
                  Plan New Trip
                </Button>
              ) : (
                <Button
                  variant="contained"
                  onClick={activeStep === 1 ? handlePaymentSubmit : handleNext}
                  disabled={
                    loading ||
                    (activeStep === 1 &&
                      (!paymentDetails.cardNumber ||
                        !paymentDetails.expiryDate ||
                        !paymentDetails.cvv ||
                        !paymentDetails.cardholderName))
                  }
                >
                  {activeStep === 1 ? (loading ? 'Processing...' : 'Confirm Payment') : 'Next'}
                </Button>
              )}
            </Box>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default Checkout;
